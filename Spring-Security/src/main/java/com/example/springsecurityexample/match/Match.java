package com.example.springsecurityexample.match;

import com.example.springsecurityexample.member.Member;
import com.example.springsecurityexample.member.Profile;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter @EqualsAndHashCode(of = "matchId")
@Entity
public class Match {
    @Id @GeneratedValue
    private Long matchId;
    private Long uid1;// replace uid
    private Long uid2;// replace matchingId
    private Boolean isUid2Team;
    private LocalDateTime matchingDate;
    private Boolean matchingRequest;
    private Boolean matchSuccess;

    @ManyToOne
    @JoinColumn(name = "id", referencedColumnName = "id")
    private Profile profile;


    //TODO : update() - isUid2Team field
}
