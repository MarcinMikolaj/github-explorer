package com.explorer.controller.explorer.impl;

import com.explorer.controller.explorer.GithubExplorerControllerApi;
import com.explorer.model.GitHubRepository;
import com.explorer.service.GithubExplorerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GithubExplorerController implements GithubExplorerControllerApi {
    private final GithubExplorerService githubExplorerService;

    @Override
    public ResponseEntity<List<GitHubRepository>> getAllRepositories(String username) {
        log.info("Received request for get repository (username: {})", username);
        return new ResponseEntity<>(githubExplorerService.receiveGithubRepositories(username), HttpStatus.OK);
    }
}
