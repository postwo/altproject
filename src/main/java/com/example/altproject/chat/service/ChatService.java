package com.example.altproject.chat.service;

import com.example.altproject.chat.domain.ChatMessage;
import com.example.altproject.chat.domain.ChatParticipant;
import com.example.altproject.chat.domain.ChatRoom;
import com.example.altproject.chat.domain.ReadStatus;
import com.example.altproject.chat.dto.ChatMessageDto;
import com.example.altproject.chat.dto.ChatRoomListResDto;
import com.example.altproject.chat.dto.MyChatListResDto;
import com.example.altproject.chat.repository.ChatMessageRepository;
import com.example.altproject.chat.repository.ChatParticipantRepository;
import com.example.altproject.chat.repository.ChatRoomRepository;
import com.example.altproject.chat.repository.ReadStatusRepository;
import com.example.altproject.domain.member.Member;
import com.example.altproject.repository.BoardRepository;
import com.example.altproject.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final ChatParticipantRepository chatParticipantRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MemberRepository memberRepository;


    public void saveMessage(Long roomId, ChatMessageDto chatMessageReqDto){
//        채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));

//        보낸사람조회
        Member sender = memberRepository.findByEmail(chatMessageReqDto.getSenderEmail()).orElseThrow(()-> new EntityNotFoundException("member cannot be found"));

//        메시지저장
        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageReqDto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);
//        사용자별로 읽음여부 저장
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant c : chatParticipants){
            ReadStatus readStatus = ReadStatus.builder()
                    .chatRoom(chatRoom)
                    .member(c.getMember())
                    .chatMessage(chatMessage)
                    .isRead(c.getMember().equals(sender))
                    .build();
            readStatusRepository.save(readStatus);
        }
    }

    public Long createGroupRoom(String chatRoomName){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));

//        채팅방 생성
        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);
//        채팅참여자로 개설자를 추가
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);

        // 🔥 생성된 roomId 반환
        return chatRoom.getId();
    }

    public List<ChatRoomListResDto> getGroupchatRooms(){
        List<ChatRoom> chatRooms = chatRoomRepository.findByIsGroupChat("Y");
        List<ChatRoomListResDto> dtos = new ArrayList<>();
        for(ChatRoom c : chatRooms){
            ChatRoomListResDto dto = ChatRoomListResDto
                    .builder()
                    .roomId(c.getId())
                    .roomName(c.getName())
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public void addParticipantToGroupChat(Long roomId){
//        채팅방조회
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
//        member조회 = 현재 로그인한 사용자 확인
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        if(chatRoom.getIsGroupChat().equals("N")){
            throw new IllegalArgumentException("그룹채팅이 아닙니다.");
        }
//        이미 참여자인지 검증
        Optional<ChatParticipant> participant = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member);
        if(!participant.isPresent()){
            addParticipantToRoom(chatRoom, member);
        }
    }

//        ChatParticipant객체생성 후 저장
    public void addParticipantToRoom(ChatRoom chatRoom, Member member){
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    public List<ChatMessageDto> getChatHistory(Long roomId){
//        내가 해당 채팅방의 참여자가 아닐경우 에러
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        boolean check = false;
        for(ChatParticipant c : chatParticipants){
            if(c.getMember().equals(member)){
                check = true;
            }
        }
        if(!check)throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");
//        특정 room에 대한 message조회
        List<ChatMessage> chatMessages = chatMessageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
        List<ChatMessageDto> chatMessageDtos = new ArrayList<>();
        for(ChatMessage c : chatMessages){
            ChatMessageDto chatMessageDto = ChatMessageDto.builder()
                    .message(c.getContent())
                    .senderEmail(c.getMember().getEmail())
                    .build();
            chatMessageDtos.add(chatMessageDto);
        }
        return chatMessageDtos;
    }

    public boolean isRoomPaticipant(String email, Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        for(ChatParticipant c : chatParticipants){
            if(c.getMember().equals(member)){
                return true;
            }
        }
        return false;
    }

    public void messageRead(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
        for(ReadStatus r : readStatuses){
            r.updateIsRead(true);
        }
    }

    public List<MyChatListResDto> getMyChatRooms(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        List<MyChatListResDto> chatListResDtos = new ArrayList<>();
        for(ChatParticipant c : chatParticipants){
            Long count = readStatusRepository.countByChatRoomAndMemberAndIsReadFalse(c.getChatRoom(), member);
            MyChatListResDto dto = MyChatListResDto.builder()
                    .roomId(c.getChatRoom().getId())
                    .roomName(c.getChatRoom().getName())
                    .isGroupChat(c.getChatRoom().getIsGroupChat())
                    .unReadCount(count)
                    .build();
            chatListResDtos.add(dto);
        }
        return chatListResDtos;
    }

    public void leaveGroupChatRoom(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        if(chatRoom.getIsGroupChat().equals("N")){
            throw new IllegalArgumentException("단체 채팅방이 아닙니다.");
        }
        ChatParticipant c = chatParticipantRepository.findByChatRoomAndMember(chatRoom, member).orElseThrow(()->new EntityNotFoundException("참여자를 찾을 수 없습니다."));
        chatParticipantRepository.delete(c);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);
        if(chatParticipants.isEmpty()){
            chatRoomRepository.delete(chatRoom);
        }
    }



    /**
     * 특정 채팅방의 이전 대화내용을 페이징하여 가져온다.
     * @param roomId 채팅방 ID
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지당 메시지 수
     * @return 메시지 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getPreviousMessages(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByIdDesc(roomId, pageable);

        return messages.stream()
                .map(message -> {
                    // ChatMessageDto의 빌더를 사용하여 안전하게 객체 생성
                    return ChatMessageDto.builder()
                            .roomId(message.getChatRoom().getId())
                            .senderEmail(message.getMember().getEmail()) // getSender() -> getMember().getEmail()
                            .message(message.getContent()) // getMessage() -> getContent()
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 마지막으로 읽은 메시지 이후에 온 읽지 않은 메시지 수를 계산한다.
     * @param roomId 채팅방 ID
     * @param lastMessageId 사용자가 마지막으로 읽은 메시지의 ID
     * @return 읽지 않은 메시지 수
     */
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long roomId, Long lastMessageId) {
        if (lastMessageId == null || lastMessageId == 0) {
            // 사용자가 방에 처음 들어왔거나, 읽은 기록이 없는 경우 모든 메시지를 카운트할 수 있으나,
            // 클라이언트에서 관리하는 것이 더 효율적이므로 0을 반환하거나 초기 메시지 수를 제한할 수 있다.
            // 여기서는 lastMessageId가 있어야만 카운트하도록 가정한다.
            return chatMessageRepository.countByChatRoomIdAndIdGreaterThan(roomId, 0L);
        }
        return chatMessageRepository.countByChatRoomIdAndIdGreaterThan(roomId, lastMessageId);
    }



//    public Long getOrCreatePrivateRoom(Long otherMemberId){
//        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
//        Member otherMember = memberRepository.findById(otherMemberId).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
//
////        나와 상대방이 1:1채팅에 이미 참석하고 있다면 해당 roomId return
//        Optional<ChatRoom> chatRoom = chatParticipantRepository.findExistingPrivateRoom(member.getId(), otherMember.getId());
//        if(chatRoom.isPresent()){
//            return chatRoom.get().getId();
//        }
////        만약에 1:1채팅방이 없을경우 기존 채팅방 개설
//        ChatRoom newRoom = ChatRoom.builder()
//                .isGroupChat("N")
//                .name(member.getNickname() + "-" + otherMember.getNickname())
//                .build();
//        chatRoomRepository.save(newRoom);
////        두사람 모두 참여자로 새롭게 추가
//        addParticipantToRoom(newRoom, member);
//        addParticipantToRoom(newRoom, otherMember);
//
//        return newRoom.getId();
//    }
}

