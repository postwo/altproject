package com.example.altproject.domain.member.status;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserStatus {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN");

    private final String description;
}
