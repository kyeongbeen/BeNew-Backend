package com.example.springsecurityexample.projectProfileCard.controller;

import com.example.springsecurityexample.projectProfileCard.ProjectProfileCard;
import com.example.springsecurityexample.projectProfileCard.dto.ProjectProfileCardRequestDto;
import com.example.springsecurityexample.projectProfileCard.repository.ProjectProfileCardRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;


@Controller
@RequiredArgsConstructor //DI
@RequestMapping(value = "/api", produces = MediaTypes.HAL_JSON_VALUE)
public class ProjectProfileCardController {

    private final ModelMapper modelMapper;

    private final ProjectProfileCardRepository projectProfileCardRepository;

    // 문제점 : 현재 room의 id를 만들때 알고 있어야 함
    @PostMapping("/post/project-profile-card")
    public ResponseEntity CreateProjectProfileCard (@RequestBody ProjectProfileCardRequestDto projectProfileCardRequestDto) {
        ProjectProfileCard card = modelMapper.map(projectProfileCardRequestDto, ProjectProfileCard.class);
        ProjectProfileCard currentCard = projectProfileCardRepository.save(card);
        return ResponseEntity.ok(currentCard);
    }

    @GetMapping("/get/project-profile-card-list/{roomId}")
    public ResponseEntity ReadProjectProfileCardsInRoom (@PathVariable Integer roomId) {
        List<ProjectProfileCard> cards = projectProfileCardRepository.findAllByRoomId(roomId);
        if (cards.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/get/project-profile-card/{projectId}")
    public ResponseEntity ReadProjectProfileCard (@PathVariable Integer projectId) {
        ProjectProfileCard card = projectProfileCardRepository.findById(projectId)
                .orElseThrow(() -> new NullPointerException("해당 아이디가 존재하지 않습니다."));
        return ResponseEntity.ok(card);
    }

    @PatchMapping("/patch/project-profile-card/{projectId}")
    public ResponseEntity ReadProjectProfileCard (@PathVariable Integer projectId,
                                                  @RequestBody ProjectProfileCardRequestDto projectProfileCardRequestDto) throws SQLException
    {
        ProjectProfileCard card = projectProfileCardRepository.findById(projectId)
                .orElseThrow(() -> new NullPointerException("해당 아이디가 존재하지 않습니다."));
        card.update(projectProfileCardRequestDto);
        projectProfileCardRepository.save(card);
        return ResponseEntity.ok(card);
    }

    @DeleteMapping("/delete/project-profile-card/{projectId}")
    public ResponseEntity DeleteProjectProfileCard (@PathVariable Integer projectId) {
        ProjectProfileCard card = projectProfileCardRepository.findById(projectId)
                .orElseThrow(() -> new NullPointerException("해당 아이디가 존재하지 않습니다."));
        projectProfileCardRepository.delete(card);
        return ResponseEntity.ok(card);
    }
}
