package com.example.altproject.dto.response;

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
    private int favoriteCount;
    private int viewCount;
    private Set<String> hashtags;
    private List<String> imageUrls;


    public static BoardResponse createResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
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

    public static BoardResponse getResponse(Board board) {
        return BoardResponse.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
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
