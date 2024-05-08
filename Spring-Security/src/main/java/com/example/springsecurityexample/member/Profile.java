package com.example.springsecurityexample.member;

import com.example.springsecurityexample.team.Team;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    @Id
    private Long id;

    private String nickname;
    private String instruction;
    private String role;
    private boolean projectExperience;
    private String personalLink;
    private String photo;
    private int peer=50;

    @OneToOne
    @JoinColumn(name = "member_id") // Member 엔티티와의 관계를 매핑
    private Member member;

    // 매칭을 위해 추가함
    @OneToMany(mappedBy = "profile")
    private List<TechnologyLevel> technologyLevel;

    //team과 profile을 n:n 매핑
    @ManyToMany(mappedBy = "profiles")
    private List<Team> teams;

    public Profile(Member member) {
        this.member = member;
    }
}
