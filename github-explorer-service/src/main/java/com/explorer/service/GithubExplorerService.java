package com.explorer.service;

import com.explorer.model.GitHubRepository;
import java.util.List;

public interface GithubExplorerService {

    List<GitHubRepository> receiveGithubRepositories(String username);

}
