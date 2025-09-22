package com.example.altproject.domain.hashtag;

import com.example.altproject.domain.board.Board;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Entity
@Builder
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "hashtagName", unique = true),
})
@NoArgsConstructor
@AllArgsConstructor
public class HashTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @ToString.Exclude
    @ManyToMany(mappedBy = "hashtags", fetch = FetchType.LAZY)
    private Set<Board> boards = new LinkedHashSet<>();

    @Setter
    @Column(nullable = false) private String hashtagName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HashTag that)) return false;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
