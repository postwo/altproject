package com.example.altproject.domain.board;

import com.example.altproject.common.auditing.AuditingFields;
import com.example.altproject.domain.hashtag.HashTag;
import com.example.altproject.domain.image.Image;
import com.example.altproject.domain.member.Member;
import com.example.altproject.dto.request.BoardRequest;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Entity
@Builder
@ToString(callSuper = true) // reports added via @ToString.Exclude
@Table(indexes = {
        @Index(columnList = "title")
})
@NoArgsConstructor
@AllArgsConstructor
public class Board extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    private String content;

    private int totalPrice;

    private String address;

    private int maxParticipants;

    private int favoriteCount;

    private int viewCount;

    private String writerEmail;

    @Setter
    private boolean isLiked;

    @Builder.Default
    @ToString.Exclude
    @JoinTable(
            name = "board_hashtag",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<HashTag> hashtags = new LinkedHashSet<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new ArrayList<>();

    // BoardReport와의 1:N 관계 추가
    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "reportedBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardReport> reports = new ArrayList<>();


    public void increaseViewCount() {
        this.viewCount ++;
    }

    public void increaseFavoriteCount() {
        this.favoriteCount ++;
    }

    public void decreaseFavoriteCount() {
        this.favoriteCount --;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(id, board.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public static Board create(BoardRequest request, Member author){
        return Board.builder()
                .title(request.getTitle())
                .address(request.getAddress())
                .totalPrice(request.getTotalPrice())
                .content(request.getContent())
                .writerEmail(author.getEmail())
                .maxParticipants(request.getMaxParticipants())
                .build();
    }

    public void update(BoardRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.address = request.getAddress();
        this.totalPrice = request.getTotalPrice();
        this.maxParticipants = request.getMaxParticipants();
    }
}
