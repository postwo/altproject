package com.example.altproject.domain.image;

import com.example.altproject.domain.board.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Objects;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "image")
@ToString(exclude = "board")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id") // DB 외래 키 컬럼 이름 명시
    private Board board;

    private String image;

    public Image(Board board, String image) {
        this.board = board;
        this.image = image;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image that = (Image) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
