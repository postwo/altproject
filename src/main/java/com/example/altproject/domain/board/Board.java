package com.example.altproject.domain.board;

import com.example.altproject.common.auditing.AuditingFields;
import com.example.altproject.domain.hashtag.HashTag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Builder
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title")
})
public class Board extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    private String content;

    private int favoriteCount;

    private int viewCount;

    @Builder.Default
    @ToString.Exclude
    @JoinTable(
            name = "board_hashtag",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id")
    )
    @ManyToMany(fetch = FetchType.LAZY,cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<HashTag> hashtags = new LinkedHashSet<>();


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
}
