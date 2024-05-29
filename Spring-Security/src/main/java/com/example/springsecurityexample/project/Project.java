package com.example.springsecurityexample.project;

import com.example.springsecurityexample.member.Profile;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "projectId")
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;


    private String projectName;
    private LocalDate creationDate;
    private int numberOfMembers;
    private Long projectManager;
    private String projectOneLineIntroduction;
    private String projectIntroduction;
    private boolean projectStarted;
    private LocalDate projectStartDate;
    private LocalDate projectDeadlineDate;
    private Long views = 0L;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "project_member_profiles",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "profile_id")
    )
    private List<Profile> profiles;

}
