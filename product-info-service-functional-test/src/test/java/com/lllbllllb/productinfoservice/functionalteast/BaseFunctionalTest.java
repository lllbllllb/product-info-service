package com.lllbllllb.productinfoservice.functionalteast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lllbllllb.productinfoservice.ProductInfoServiceApplication;
import com.lllbllllb.productinfoservice.repositorylocal.ProductInfoServiceBuildInfoRepository;
import com.lllbllllb.productinfoservice.repositorylocal.ProductInfoServiceProductInfoRepository;
import com.lllbllllb.productinfoservice.repositorylocal.ProductInfoServiceRoundRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    classes = ProductInfoServiceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
public abstract class BaseFunctionalTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ProductInfoServiceBuildInfoRepository buildInfoRepository;

    @Autowired
    protected ProductInfoServiceProductInfoRepository productInfoRepository;

    @Autowired
    protected ProductInfoServiceRoundRepository roundRepository;

    @LocalServerPort
    protected int localServerPort;

    @BeforeEach
    public void setUp() {
        RestAssured.port = localServerPort;
    }

    protected void clearAllTables() {
        buildInfoRepository.deleteAll().block();
        productInfoRepository.deleteAll().block();
        roundRepository.deleteAll().block();
    }
}
