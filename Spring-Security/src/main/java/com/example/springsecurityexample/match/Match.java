package com.example.springsecurityexample.match;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter @EqualsAndHashCode(of = "matchId")
@Entity
public class Match {
    @Id @GeneratedValue
    private Integer matchId;
    private int uid1;// replace uid
    private int uid2;// replace matchingId
    private Boolean isUid2Team;
    private LocalDateTime matchingDate;
    private Boolean matchingRequest;
    private Boolean matchSuccess;


//    @ManyToOne
//    private Account manager;
}
