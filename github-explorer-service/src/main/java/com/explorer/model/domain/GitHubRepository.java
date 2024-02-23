package com.explorer.model.domain;

import lombok.Builder;
import java.util.List;

@Builder
public record GitHubRepository(
        String repositoryName,
        String ownerLogin,
        List<GithubBranch> branches) { }



