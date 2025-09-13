package com.example.altproject.repository;

import com.example.altproject.domain.member.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths ={"memberRoleList"})
    @Query("select m from Member m where m.email = :email")
    Optional<Member> getWithRoles(String email);

    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);


}
