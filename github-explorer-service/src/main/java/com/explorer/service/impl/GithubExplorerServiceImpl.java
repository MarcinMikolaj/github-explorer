package com.explorer.service.impl;

import com.explorer.dto.Branch;
import com.explorer.dto.Repository;
import com.explorer.model.*;
import com.explorer.service.GithubExplorerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubExplorerServiceImpl implements GithubExplorerService {
    @Value("${github.api.address}")
    String gitHubApiAddress;
    private final Gson gson;

    // TODO: replace method for processBranches
    // TODO: build url in correct way
    @Override
    public List<GitHubRepository> receiveGithubRepositories(String username) {
        String url = gitHubApiAddress + "/users/" + username + "/repos";
        url = buildUri(url, RepositoryType.OWNER, SortType.CREATED, SortDirectionType.ASC, 1, 30);
        String result = invokeWebClient(HttpMethod.GET, url);
        Type repositoryListType = new TypeToken<List<Repository>>(){}.getType();
        List<Repository> repositories = gson.fromJson(result, repositoryListType);
        return repositories.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> prepareGitHubRepository(repo.name(), repo.owner().login(),
                        processBranches(repo.branchesUrl())))
                .collect(Collectors.toList());
    }

    private List<GithubBranch> processBranches(String branchUrl){
        String result = invokeWebClient(HttpMethod.GET, branchUrl.replaceAll("\\{\\/branch\\}", ""));
        List<Branch> branches = gson.fromJson(result, new TypeToken<List<Branch>>(){}.getType());
        return branches.stream()
                .map(branch -> new GithubBranch(branch.name(), branch.commit().sha()))
                .collect(Collectors.toList());
    }

    private String invokeWebClient(HttpMethod method, String uri){
        log.info("Invoke web client for: {}", uri);
        return WebClient.create()
                .method(method)
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(clientResponse -> onExchangeMono(clientResponse))
                .timeout(Duration.ofSeconds(20))
                .retry(5)
                .block();
    }

    private Mono<String> onExchangeMono(ClientResponse response){
        return response.statusCode().is2xxSuccessful()
                ? response.bodyToMono(String.class)
                : response.statusCode().is4xxClientError()
                  ? Mono.just("Error response for WebClient")
                  : response.createException().flatMap(Mono::error);
    }

    private String buildUri(String baseUrl, RepositoryType repositoryType, SortType sortType,
                            SortDirectionType sortDirectionType, int page, int perPage){
        return UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("type", repositoryType.toString().toLowerCase())
                .queryParam("sort", sortType.toString().toLowerCase())
                .queryParam("direction", sortDirectionType.toString().toLowerCase())
                .queryParam("per_page", perPage)
                .queryParam("page", page)
                .toUriString();
    }

    private GitHubRepository prepareGitHubRepository(String repositoryName, String ownerLogin, List<GithubBranch> branches){
        return GitHubRepository.builder()
                .repositoryName(repositoryName)
                .ownerLogin(ownerLogin)
                .branches(branches)
                .build();
    }

}
