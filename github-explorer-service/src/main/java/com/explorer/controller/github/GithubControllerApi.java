package com.explorer.controller.github;

import com.explorer.model.domain.GitHubRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/v1/api/repository")
public interface GithubControllerApi {

    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GitHubRepository>> getAllRepositories(
            @RequestParam String username);
}
