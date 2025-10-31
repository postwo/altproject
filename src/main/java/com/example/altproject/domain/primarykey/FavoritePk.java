package com.example.altproject.domain.primarykey;

import com.example.altproject.domain.board.Board;
import com.example.altproject.domain.member.Member;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FavoritePk implements Serializable {
    private Member member;

    private Board board;
}