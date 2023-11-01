package com.example.springsecurityexample.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nickname;
    private String instruction;
    private String role;
    private boolean projectExperience;
    private String personalLink;
    private String photo;

    @OneToOne
    @JoinColumn(name = "member_id") // Member 엔티티와의 관계를 매핑
    private Member member;

    public Profile(Member member) {
        this.nickname = member.getNickname();
        this.member = member;
    }
}
