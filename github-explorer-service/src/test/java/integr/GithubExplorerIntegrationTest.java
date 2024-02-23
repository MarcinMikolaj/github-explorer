package integr;

import com.explorer.GithubExplorerServiceApplication;
import com.explorer.infrastructure.exceptions.GithubException;
import com.explorer.model.domain.GitHubRepository;
import com.explorer.service.GithubService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        classes = {GithubExplorerServiceApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 8081)
@TestPropertySource(properties = {
        "tracker.url=http://localhost:${wiremock.server.port}"
})
public class GithubExplorerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GithubService githubService;
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("app.github.api.base-url", () -> "http://localhost:8081");
    }

    @Test
    void getRepositoriesShouldReturnProperGithubRepositoryList() throws IOException {
        final String username = "MarcinMikolaj";
        String url = "/users/MarcinMikolaj/repos";
        String expectedGithubResponse =
                getJsonFromResource("representation/ExampleGithubRepository.json");
        String expectedRepository =
                getJsonFromResource("representation/ExampleRepository.json");
        stubFor(get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(expectedRepository)));
        List<GitHubRepository> providedGithubRepositories = githubService.getRepositories(username);
        assertEquals(providedGithubRepositories, objectMapper.readValue(expectedGithubResponse, objectMapper.getTypeFactory()
                        .constructCollectionType(List.class, GitHubRepository.class)));
    }

    @Test
    void getRepositoriesShouldThrowReturnGithubException() throws IOException {
        final String username = "MarcinMikolaj";
        String url = "/users/MarcinMikolaj/repos";
        String expectedRepository =
                getJsonFromResource("representation/ExampleRepository.json");
        stubFor(get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(400)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(expectedRepository)));
        assertThrows(GithubException.class, () -> githubService.getRepositories(username));
    }

    @Test
    void getRepositoriesShouldThrowReturnGithubIfUsernameIsNull() throws IOException {
        String url = "/users/MarcinMikolaj/repos";
        String expectedRepository =
                getJsonFromResource("representation/ExampleRepository.json");
        stubFor(get(urlPathEqualTo(url))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(expectedRepository)));
        assertThrows(GithubException.class, () -> githubService.getRepositories(null));
    }

    private String getJsonFromResource(String path) throws IOException {
        final File studentFileRepresentation = new ClassPathResource(path).getFile();
        return Files.readString(studentFileRepresentation.toPath());
    }

}
