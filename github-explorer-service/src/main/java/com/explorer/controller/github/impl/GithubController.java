package com.explorer.controller.github.impl;

import com.explorer.controller.github.GithubControllerApi;
import com.explorer.model.domain.GitHubRepository;
import com.explorer.service.GithubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GithubController implements GithubControllerApi {
    private final GithubService githubService;

    @Override
    public ResponseEntity<List<GitHubRepository>> getAllRepositories(String username) {
        log.info("Received request for get repository (username: {})", username);
        return new ResponseEntity<>(githubService.getRepositories(username), HttpStatus.OK);
    }
}
