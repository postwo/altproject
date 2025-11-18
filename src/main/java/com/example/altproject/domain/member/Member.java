package com.example.altproject.domain.member;

import com.example.altproject.domain.member.status.MemberRole;
import com.example.altproject.domain.member.status.MemberStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Builder
@ToString(exclude = "memberRoleList")
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(length = 100 ,nullable = false ,unique = true)
    private String email;

    @Column(length = 255 )
    private String password;

    @Column(length = 50 ,nullable = false)
    @Setter
    private String nickname;

    @Builder.Default
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "member_roles",
            joinColumns = @JoinColumn(name = "member_id")
    )
    private List<MemberRole> memberRoleList = new ArrayList<>();

    private boolean social;

    private String socialUsername;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime modifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member that = (Member) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    public void addRole(MemberRole role) {
        memberRoleList.add(role);
    }

    public void clearRole() {
        memberRoleList.clear();
    }

    public void changeSocial(boolean social) {
        this.social = social;
    }

    public static Member createMember(String email, String encodedPassword, String nickname) {
        return Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .status(MemberStatus.ACTIVE)
                .memberRoleList(new ArrayList<>(List.of(MemberRole.USER)))
                .build();
    }

    public static Member createSocialMember(String email, String nickname , String socialUsername) {
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .password(null)
                .status(MemberStatus.ACTIVE)
                .socialUsername(socialUsername)
                .memberRoleList(new ArrayList<>(List.of(MemberRole.USER)))
                .social(true)
                .build();
    }


}
