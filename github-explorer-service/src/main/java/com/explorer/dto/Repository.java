package com.explorer.dto;

import com.google.gson.annotations.SerializedName;

public record Repository (
        String id,
        String name,
        boolean fork,
        Owner owner,
        @SerializedName("branches_url")
        String branchesUrl) {}
