package com.explorer.service;

import com.explorer.model.domain.GitHubRepository;

import java.util.List;

public interface GithubService {

    List<GitHubRepository> getRepositories(String username);
}
