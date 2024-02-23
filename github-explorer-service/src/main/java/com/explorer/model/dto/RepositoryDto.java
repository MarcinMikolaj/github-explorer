package com.explorer.model.dto;

import com.google.gson.annotations.SerializedName;

public record RepositoryDto(
        String id,
        String name,
        boolean fork,
        Owner owner,
        @SerializedName("branches_url")
        String branchesUrl) {}
