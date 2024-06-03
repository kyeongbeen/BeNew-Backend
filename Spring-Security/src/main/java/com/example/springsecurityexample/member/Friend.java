//package com.example.springsecurityexample.member;
//
//import lombok.*;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@Setter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class Friend {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "member_id")
//    private Member member;
//
//    @ManyToOne
//    @JoinColumn(name = "friend_id")
//    private Member friend;
//
//    // 친구 상태 (예: 요청 대기, 수락됨 등)
//    private String status;
//
//}
package com.example.springsecurityexample.member;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToOne
    @JoinColumn(name = "friend_profile_id")
    private Profile friendProfile;

    private String status;
}
