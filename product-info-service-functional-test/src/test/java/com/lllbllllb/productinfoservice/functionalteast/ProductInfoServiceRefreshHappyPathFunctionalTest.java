package com.lllbllllb.productinfoservice.functionalteast;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.lllbllllb.productinfoservice.controller.model.BuildInfoDto;
import org.apache.commons.text.StringSubstitutor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import reactor.test.StepVerifier;
import wiremock.org.apache.hc.core5.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.REFRESH_URL;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.IF_MODIFIED_SINCE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.http.MediaType.TEXT_XML_VALUE;

public class ProductInfoServiceRefreshHappyPathFunctionalTest extends BaseFunctionalTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
        clearAllTables();
    }

    @AfterEach
    public void cleanUp() {
        clearAllTables();
    }

    @Test
    void shouldRefresh() {
        // given
        stubFor(get("/updates/updates.xml")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_XML_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, TEXT_XML_VALUE)
                .withBodyFile("stubs/updates.xml")));
        var iuJsonResponse = StringSubstitutor.replace(loadResourceAsString("__files/stubs/IUreleases.json"), Map.of("wiremockServerPort", wiremockSeverPort));
        stubFor(get("/products/releases?code=IU")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(iuJsonResponse)));
        var isJsonResponse = StringSubstitutor.replace(loadResourceAsString("__files/stubs/ICreleases.json"), Map.of("wiremockServerPort", wiremockSeverPort));
        stubFor(get("/products/releases?code=IC")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(isJsonResponse)));
        var gwJsonResponse = StringSubstitutor.replace(loadResourceAsString("__files/stubs/GWreleases.json"), Map.of("wiremockServerPort", wiremockSeverPort));
        stubFor(get("/products/releases?code=GW")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_JSON_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .withBody(gwJsonResponse)));
        stubFor(get("/idea/gateway/JetBrainsGateway-222.3345.108.tar.gz")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_OCTET_STREAM_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .withBodyFile("stubs/JetBrainsGateway-222.3345.108.tar.gz")));
        stubFor(get("/idea/gateway/JetBrainsGateway-222.3345.108.tar.gz.sha256")
            .withHeader(ACCEPT, new EqualToPattern(TEXT_PLAIN_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("bed468e47bb21d09aebea19319035f51ebc49ad70587149ee4fddce6225a01bc *JetBrainsGateway-2022.2.tar.gz")));
        stubFor(get("/idea/gateway/JetBrainsGateway-221.5921.22.tar.gz")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_OCTET_STREAM_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .withBodyFile("stubs/JetBrainsGateway-221.5921.22.tar.gz")));
        stubFor(get("/idea/gateway/JetBrainsGateway-221.5921.22.tar.gz.sha256")
            .withHeader(ACCEPT, new EqualToPattern(TEXT_PLAIN_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("3e5a92831079e27e70960855fd5c40ee30737aa8243872abe33bf82d0492a12c *JetBrainsGateway-2022.1.4.tar.gz")));
        stubFor(get("/idea/ideaIC-2022.2.tar.gz")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_OCTET_STREAM_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .withBodyFile("stubs/intellij-community-idea-222.3345.118.tar.gz")));
        stubFor(get("/idea/ideaIC-2022.2.tar.gz.sha256")
            .withHeader(ACCEPT, new EqualToPattern(TEXT_PLAIN_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("b84367589a73e516a2d3900cafe9c40be6d19c669b25885ba31455e3b5686386 *ideaIC-2022.2.tar.gz")));
        stubFor(get("/idea/ideaIC-2022.1.4.tar.gz")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_OCTET_STREAM_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .withBodyFile("stubs/intellij-community-idea-221.6008.13.tar.gz")));
        stubFor(get("/idea/ideaIC-2022.1.4.tar.gz.sha256")
            .withHeader(ACCEPT, new EqualToPattern(TEXT_PLAIN_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("7534602cad0eed5318cd8f061975e425d71def989323a2597be7240c68b344de *ideaIC-2022.1.4.tar.gz")));
        stubFor(get("/idea/ideaIU-2022.2.tar.gz")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_OCTET_STREAM_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .withBodyFile("stubs/ideaIU-222.3345.118.tar.gz")));
        stubFor(get("/idea/ideaIU-2022.2.tar.gz.sha256")
            .withHeader(ACCEPT, new EqualToPattern(TEXT_PLAIN_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("0ee3b0c991f98bd29f50d71db7978bffc4b4eef2017224d2a7b09677d9766209 *ideaIU-2022.2.tar.gz")));
        stubFor(get("/idea/ideaIU-2022.1.4.tar.gz")
            .withHeader(ACCEPT, new EqualToPattern(APPLICATION_OCTET_STREAM_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_OCTET_STREAM_VALUE)
                .withBodyFile("stubs/ideaIU-221.6008.13.tar.gz")));
        stubFor(get("/idea/ideaIU-2022.1.4.tar.gz.sha256")
            .withHeader(ACCEPT, new EqualToPattern(TEXT_PLAIN_VALUE))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_OK)
                .withHeader(HttpHeaders.CONTENT_TYPE, TEXT_PLAIN_VALUE)
                .withBody("a6aef43213bd5ba8b6e6d5a146b0232248ca91c26460cca0fc5cc5aca862275d *ideaIU-2022.1.4.tar.gz")));

        // when
        var response = given()
            .when()
            .post(REFRESH_URL)
            .then()
            .statusCode(200)
            .log().all()
            .extract().body().as(BuildInfoDto[].class);

        // then
        assertAll(
            () -> assertEquals(6, response.length),
            () -> Awaitility.await()
                .atMost(Duration.ofSeconds(6))
                .untilAsserted(() -> StepVerifier.create(productInfoRepository.count())
                    .consumeNextWith(count -> assertEquals(6, count))
                    .verifyComplete()),
            () -> StepVerifier.create(productInfoRepository.findByProductCodeAndFullNumber("GW", "222.3345.108"))
                .consumeNextWith(pi -> assertTrue(pi.getProductInfo().asString().contains("222.3345.108")))
                .verifyComplete(),
            () -> StepVerifier.create(productInfoRepository.findByProductCodeAndFullNumber("GW", "221.5921.22"))
                .consumeNextWith(pi -> assertTrue(pi.getProductInfo().asString().contains("221.5921.22")))
                .verifyComplete(),
            () -> StepVerifier.create(productInfoRepository.findByProductCodeAndFullNumber("IC", "222.3345.118"))
                .consumeNextWith(pi -> assertTrue(pi.getProductInfo().asString().contains("222.3345.118")))
                .verifyComplete(),
            () -> StepVerifier.create(productInfoRepository.findByProductCodeAndFullNumber("IC", "221.6008.13"))
                .consumeNextWith(pi -> assertTrue(pi.getProductInfo().asString().contains("221.6008.13")))
                .verifyComplete(),
            () -> StepVerifier.create(productInfoRepository.findByProductCodeAndFullNumber("IU", "222.3345.118"))
                .consumeNextWith(pi -> assertTrue(pi.getProductInfo().asString().contains("222.3345.118")))
                .verifyComplete(),
            () -> StepVerifier.create(productInfoRepository.findByProductCodeAndFullNumber("IU", "221.6008.13"))
                .consumeNextWith(pi -> assertTrue(pi.getProductInfo().asString().contains("221.6008.13")))
                .verifyComplete()
        );
    }

    @Test
    void shouldSkipRefreshDueToNotModified() {
        // given
        var lastCheck = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.of("GMT"));
        var lastCheckFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US).withZone(ZoneId.of("GMT"));
        var lastCheckValue = lastCheckFormatter.format(lastCheck);

        // when
        stubFor(get("/updates/updates.xml")
            .withHeader(IF_MODIFIED_SINCE, new EqualToPattern(lastCheckValue))
            .willReturn(aResponse()
                .withStatus(HttpStatus.SC_NOT_MODIFIED)));

        // then
        given()
            .when()
            .post(REFRESH_URL)
            .then()
            .statusCode(200)
            .log().all()
            .body("$", Matchers.hasSize(0));
    }
}
