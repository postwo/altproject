package com.example.altproject.domain.primarykey;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FavoritePk implements Serializable {

    private Long member;
    private Long board;
}