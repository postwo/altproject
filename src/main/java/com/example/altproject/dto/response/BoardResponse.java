package com.example.altproject.dto.response;

import com.example.altproject.chat.domain.ChatRoom;
import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.hashtag.HashTag;
import com.example.altproject.domain.image.Image;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {

    private Long id;
    private String title;
    private String content;
    private String address;
    private int totalPrice;
    private int favoriteCount;
    private int viewCount;
    private int maxParticipants;
    private Long chatRoomId;
    private boolean isLiked;
    private LocalDateTime createdAt;
    private String writerEmail;
    private Long reportCount;
    private Set<String> hashtags;
    private List<String> imageUrls;

    // JPQL에서 DTO로 직접 조회하기 위한 생성자 (필드 확장)
    public BoardResponse(Long id, String title, String content, String address, int totalPrice, int maxParticipants, String writerEmail, LocalDateTime createdAt, Long reportCount) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.address = address;
        this.totalPrice = totalPrice;
        this.maxParticipants = maxParticipants;
        this.writerEmail = writerEmail;
        this.createdAt = createdAt;
        this.reportCount = reportCount;
    }


    public static BoardResponse createResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .address(board.getAddress())
                .totalPrice(board.getTotalPrice())
                .content(board.getContent())
                .maxParticipants(board.getMaxParticipants())
                .favoriteCount(board.getFavoriteCount())
                .viewCount(board.getViewCount())
                .hashtags(
                        board.getHashtags().stream()
                                .map(HashTag::getHashtagName)
                                .collect(Collectors.toSet())
                )
                .imageUrls(board.getImages().stream()
                        .map(Image::getImage).
                        collect(Collectors.toList())
                )
                .build();
    }

    public static BoardResponse updateResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .address(board.getAddress())
                .totalPrice(board.getTotalPrice())
                .maxParticipants(board.getMaxParticipants())
                .content(board.getContent())
                .hashtags(
                        board.getHashtags().stream()
                                .map(HashTag::getHashtagName)
                                .collect(Collectors.toSet())
                )
                .imageUrls(board.getImages().stream()
                        .map(Image::getImage).
                        collect(Collectors.toList())
                )
                .build();
    }

    public static BoardResponse getResponseChat(Board board, ChatRoom chatRoom) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .address(board.getAddress())
                .maxParticipants(board.getMaxParticipants())
                .totalPrice(board.getTotalPrice())
                .favoriteCount(board.getFavoriteCount())
                .viewCount(board.getViewCount())
                .isLiked(board.isLiked())
                .chatRoomId(chatRoom.getId())
                .hashtags(board.getHashtags().stream()
                        .map(HashTag::getHashtagName)
                        .collect(Collectors.toSet()))
                .imageUrls(board.getImages().stream()
                        .map(Image::getImage)
                        .collect(Collectors.toList()))
                .build();
    }

    public static BoardResponse getResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .address(board.getAddress())
                .maxParticipants(board.getMaxParticipants())
                .totalPrice(board.getTotalPrice())
                .favoriteCount(board.getFavoriteCount())
                .viewCount(board.getViewCount())
                .hashtags(board.getHashtags().stream()
                        .map(HashTag::getHashtagName)
                        .collect(Collectors.toSet()))
                .imageUrls(board.getImages().stream()
                        .map(Image::getImage)
                        .collect(Collectors.toList()))
                .build();
    }

    // 이 메서드는 이제 사용되지 않으므로 삭제해도 무방합니다.
    public static BoardResponse adminBoards(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .address(board.getAddress())
                .maxParticipants(board.getMaxParticipants())
                .totalPrice(board.getTotalPrice())
                .createdAt(board.getCreatedAt())
                .writerEmail(board.getWriterEmail())
                .hashtags(board.getHashtags().stream()
                        .map(HashTag::getHashtagName)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static BoardResponse userBoardList(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .address(board.getAddress())
                .maxParticipants(board.getMaxParticipants())
                .totalPrice(board.getTotalPrice())
                .favoriteCount(board.getFavoriteCount())
                .viewCount(board.getViewCount())
                .hashtags(board.getHashtags().stream()
                        .map(HashTag::getHashtagName)
                        .collect(Collectors.toSet()))
                .imageUrls(board.getImages().stream()
                        .map(Image::getImage)
                        .collect(Collectors.toList()))
                .build();
    }


}
