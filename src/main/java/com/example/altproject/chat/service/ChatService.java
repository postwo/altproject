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
import java.util.Map;
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


    @Transactional
    public void saveMessage(Long roomId, ChatMessageDto chatMessageReqDto){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member sender = memberRepository.findByEmail(chatMessageReqDto.getSenderEmail()).orElseThrow(()-> new EntityNotFoundException("member cannot be found"));

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .member(sender)
                .content(chatMessageReqDto.getMessage())
                .build();
        chatMessageRepository.save(chatMessage);

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findByChatRoom(chatRoom);

        // ⬇️ [수정] 반복문 대신 stream과 saveAll을 사용하여 성능 개선
        List<ReadStatus> readStatusesToSave = chatParticipants.stream()
                .map(participant -> ReadStatus.builder()
                        .chatRoom(chatRoom)
                        .member(participant.getMember())
                        .chatMessage(chatMessage)
                        .isRead(participant.getMember().equals(sender))
                        .build())
                .collect(Collectors.toList());
        readStatusRepository.saveAll(readStatusesToSave);
    }

    public Long createGroupRoom(String chatRoomName){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        ChatRoom chatRoom = ChatRoom.builder()
                .name(chatRoomName)
                .isGroupChat("Y")
                .build();
        chatRoomRepository.save(chatRoom);
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);

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
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        if(chatRoom.getIsGroupChat().equals("N")){
            throw new IllegalArgumentException("그룹채팅이 아닙니다.");
        }
        if(!chatParticipantRepository.existsByChatRoomAndMember(chatRoom, member)){
            addParticipantToRoom(chatRoom, member);
        }
    }

    public void addParticipantToRoom(ChatRoom chatRoom, Member member){
        ChatParticipant chatParticipant = ChatParticipant.builder()
                .chatRoom(chatRoom)
                .member(member)
                .build();
        chatParticipantRepository.save(chatParticipant);
    }

    // ⬇️ [수정] exists 쿼리를 사용하도록 리팩토링
    @Transactional(readOnly = true)
    public boolean isRoomPaticipant(String email, Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        return chatParticipantRepository.existsByChatRoomAndMember(chatRoom, member);
    }

    @Transactional
    public void messageRead(Long roomId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId).orElseThrow(()-> new EntityNotFoundException("room cannot be found"));
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));
        List<ReadStatus> readStatuses = readStatusRepository.findByChatRoomAndMember(chatRoom, member);
        for(ReadStatus r : readStatuses){
            r.updateIsRead(true);
        }
    }

    // ⬇️ [수정] N+1 문제 해결
    @Transactional(readOnly = true)
    public List<MyChatListResDto> getMyChatRooms(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()->new EntityNotFoundException("member cannot be found"));

        List<ChatParticipant> chatParticipants = chatParticipantRepository.findAllByMember(member);
        List<ChatRoom> chatRooms = chatParticipants.stream()
                .map(ChatParticipant::getChatRoom)
                .collect(Collectors.toList());

        Map<ChatRoom, Long> unreadCounts = readStatusRepository.countByChatRoomInAndMemberAndIsReadFalse(chatRooms, member)
                .stream()
                .collect(Collectors.toMap(ReadStatusRepository.UnreadCount::getChatRoom, ReadStatusRepository.UnreadCount::getCount));

        return chatRooms.stream()
                .map(chatRoom -> MyChatListResDto.builder()
                        .roomId(chatRoom.getId())
                        .roomName(chatRoom.getName())
                        .isGroupChat(chatRoom.getIsGroupChat())
                        .unReadCount(unreadCounts.getOrDefault(chatRoom, 0L))
                        .build())
                .collect(Collectors.toList());
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

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getPreviousMessages(Long roomId, int page, int size) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!isRoomPaticipant(userEmail, roomId)) {
            throw new IllegalArgumentException("본인이 속하지 않은 채팅방입니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        List<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByIdDesc(roomId, pageable);

        return messages.stream()
                .map(message -> ChatMessageDto.builder()
                        .roomId(message.getChatRoom().getId())
                        .senderEmail(message.getMember().getEmail())
                        .message(message.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getTotalUnreadMessageCount() {
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new EntityNotFoundException("member cannot be found"));
        return readStatusRepository.countByMemberAndIsReadFalse(member);
    }
}
