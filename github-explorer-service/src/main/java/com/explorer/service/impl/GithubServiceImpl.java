package com.explorer.service.impl;

import com.explorer.infrastructure.utils.UtilsObjectMapper;
import com.explorer.model.domain.*;
import com.explorer.model.dto.Branch;
import com.explorer.model.dto.RepositoryDto;
import com.explorer.infrastructure.exceptions.GithubException;
import com.explorer.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GithubServiceImpl implements GithubService {
    @Value("${app.github.api.base-url}")
    private String baseUrl;
    private final WebClient webClient;

    @Override
    public List<GitHubRepository> getRepositories(String username) {
        var preparedUri = buildUri(baseUrl, Optional.ofNullable(username)
                .orElseThrow(() -> new GithubException("Username can't be null !")));
        var repositories = UtilsObjectMapper
                .getInstanceAsList(invokeSyncWebClient(preparedUri), RepositoryDto.class);
        return repositories.stream()
                .filter(Objects::nonNull)
                .filter(repo -> !repo.fork())
                .map(repo -> buildGitHubRepository(
                        repo.name(),
                        repo.owner().login(),
                        getBranchDetails(repo.branchesUrl())))
                .collect(Collectors.toList());
    }

    private List<GithubBranch> getBranchDetails(String branchUrl){
        String responseAsJson = invokeSyncWebClient(branchUrl.replaceAll("\\{\\/branch\\}", ""));
        List<Branch> branches = UtilsObjectMapper.getInstanceAsList(responseAsJson, Branch.class);
        return branches.stream()
                .parallel()
                .filter(Objects::nonNull)
                .map(branch -> new GithubBranch(branch.name(), branch.commit().sha()))
                .collect(Collectors.toList());
    }

    private String invokeSyncWebClient(String uri){
        return webClient.method(HttpMethod.GET)
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(this::onExchangeMono)
                .timeout(Duration.ofSeconds(20))
                .retry(3)
                .block();
    }

    private Mono<String> onExchangeMono(ClientResponse response){
        return response.statusCode().is2xxSuccessful()
                ? response.bodyToMono(String.class)
                : response.statusCode().value() == 404
                   ? response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new GithubException("GitHub username not found !")))
                   : response.bodyToMono(String.class)
                        .flatMap(body -> Mono.error(new GithubException(body)));
    }

    public String buildUri(String baseUrl, String username){
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path("users/")
                .path(username)
                .path("/repos")
                .queryParam("type", RepositoryType.OWNER.toString().toLowerCase())
                .queryParam("sort", SortType.CREATED.toString().toLowerCase())
                .queryParam("direction", SortDirectionType.ASC.toString().toLowerCase())
                .queryParam("per_page", 30)
                .queryParam("page", 1)
                .toUriString();
    }

    private GitHubRepository buildGitHubRepository(
            String repositoryName, String ownerLogin, List<GithubBranch> branches){
        return GitHubRepository.builder()
                .repositoryName(repositoryName)
                .ownerLogin(ownerLogin)
                .branches(branches)
                .build();
    }
}
