package com.example.springsecurityexample.vote.service;

import com.example.springsecurityexample.vote.Vote;
import com.example.springsecurityexample.vote.VoteContents;
import com.example.springsecurityexample.vote.dto.VoteDTO;
//import com.example.springsecurityexample.vote.repository.VoteContentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteContentsService {

//    private final VoteContentsRepository voteContentsRepository;

    public void voteContentCreator(VoteDTO voteDTO) {
        for (String content : voteDTO.getContents()) {
            VoteContents voteContents = VoteContents.builder()
                    .voteId(voteDTO.getVoteId())
                    .content(content)
                    .build();
        }
        }

}
