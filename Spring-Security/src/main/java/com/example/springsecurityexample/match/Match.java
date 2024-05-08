package com.example.springsecurityexample.match;

import com.example.springsecurityexample.member.Profile;
import com.example.springsecurityexample.member.TechnologyLevel;
import com.example.springsecurityexample.team.Team;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter @EqualsAndHashCode(of = "matchId")
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"uid1", "uid2"})
})
public class Match {
    @Id @GeneratedValue
    private Long matchId;

    private Long uid1TeamId;

    private Long uid1;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "uid2")
    private Profile profile;

    private LocalDateTime matchingDate;

    @Enumerated(EnumType.STRING)

    private MatchRequestType matchingRequest = MatchRequestType.PENDING;
    @Enumerated(EnumType.STRING)
    private MatchSuccessType matchSuccess = MatchSuccessType.PENDING;


}
