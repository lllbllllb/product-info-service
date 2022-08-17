package com.lllbllllb.productinfoservice.functionalteast;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lllbllllb.productinfoservice.ProductInfoServiceApplication;
import com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreConfigurationProperties;
import com.lllbllllb.productinfoservice.repositorylocal.ProductInfoServiceBuildInfoRepository;
import com.lllbllllb.productinfoservice.repositorylocal.ProductInfoServiceProductInfoRepository;
import com.lllbllllb.productinfoservice.repositorylocal.ProductInfoServiceRoundRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.FileCopyUtils;

import static java.nio.charset.StandardCharsets.UTF_8;

@SpringBootTest(
    classes = ProductInfoServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
public abstract class BaseFunctionalTest {

    @Value("${wiremock.server.port}")
    protected int wiremockSeverPort;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ProductInfoServiceBuildInfoRepository buildInfoRepository;

    @Autowired
    protected ProductInfoServiceProductInfoRepository productInfoRepository;

    @Autowired
    protected ProductInfoServiceRoundRepository roundRepository;

    @Autowired
    protected ProductInfoServiceCoreConfigurationProperties coreConfigurationProperties;

    @LocalServerPort
    protected int localServerPort;

    @BeforeEach
    public void setUp() {
        RestAssured.port = localServerPort;
        clearAllTables();
    }

    @AfterEach
    public void cleanUp() throws Exception {
        deleteTmpDir();
    }

    protected void clearAllTables() {
        buildInfoRepository.deleteAll().block();
        productInfoRepository.deleteAll().block();
        roundRepository.deleteAll().block();
    }

    protected void deleteTmpDir() throws Exception {
        var path = Path.of(coreConfigurationProperties.getPathToSaveTmp());

        Files.deleteIfExists(path);
    }

    protected Resource loadResource(String name) {
        return resourceLoader.getResource("classpath:%s".formatted(name));
    }

    protected String loadResourceAsString(String name) {
        var resource = loadResource(name);

        try (var reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
