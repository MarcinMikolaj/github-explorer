package com.explorer.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GitHubRepository {
    private String repositoryName;
    private String ownerLogin;
    private List<GithubBranch> branches;
}
