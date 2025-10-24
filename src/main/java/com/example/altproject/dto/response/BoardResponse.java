package com.example.altproject.dto.response;

import com.example.altproject.chat.domain.ChatRoom;
import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.hashtag.HashTag;
import com.example.altproject.domain.image.Image;
import lombok.*;

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
    private Set<String> hashtags;
    private List<String> imageUrls;


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

}
