package com.explorer.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GithubBranch {
    private String name;
    private String sha;
}
