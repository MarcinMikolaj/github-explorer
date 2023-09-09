package com.explorer.service.impl;

import com.explorer.infrastructure.utils.UtilsObjectMapper;
import com.explorer.dto.Branch;
import com.explorer.dto.Repository;
import com.explorer.infrastructure.exceptions.ResponseException;
import com.explorer.model.*;
import com.explorer.service.GithubExplorerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GithubExplorerServiceImpl implements GithubExplorerService {
    @Value("${github.api.address}")
    String gitHubApiAddress;

    @Override
    public List<GitHubRepository> receiveGithubRepositories(String username) {
        String preparedUri = buildUri(MessageFormat.format("{0}/users/{1}/repos", gitHubApiAddress, username),
                RepositoryType.OWNER, SortType.CREATED, SortDirectionType.ASC, 1, 30);
        List<Repository> repositories = UtilsObjectMapper.getInstanceAsList(invokeWebClient(HttpMethod.GET, preparedUri), Repository.class);
        return repositories.stream()
                .filter(Objects::nonNull)
                .filter(repo -> !repo.fork())
                .map(repo -> prepareGitHubRepository(repo.name(), repo.owner().login(), processBranches(repo.branchesUrl())))
                .collect(Collectors.toList());
    }

    private List<GithubBranch> processBranches(String branchUrl){
        String responseAsJson = invokeWebClient(HttpMethod.GET, branchUrl.replaceAll("\\{\\/branch\\}", ""));
        List<Branch> branches = UtilsObjectMapper.getInstanceAsList(responseAsJson, Branch.class);
        return branches.stream()
                .filter(Objects::nonNull)
                .map(branch -> new GithubBranch(branch.name(), branch.commit().sha()))
                .collect(Collectors.toList());
    }

    private String invokeWebClient(HttpMethod method, String uri){
        log.info("Invoked web client for: {}", uri);
        return WebClient.create()
                .method(method)
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(this::onExchangeMono)
                .doOnError(ex -> log.error(ex.getMessage()))
                .timeout(Duration.ofSeconds(20))
                .retry(3)
                .block();
    }

    private Mono<String> onExchangeMono(ClientResponse response){
        if (response.statusCode().is2xxSuccessful())
            return response.bodyToMono(String.class);
        else if (response.statusCode().value() == 404)
            throw new ResponseException("GitHub username not found !");
         else
            throw new ResponseException("Incorrect response from server !");
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
