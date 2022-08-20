package com.lllbllllb.productinfoservice.functionalteast;

import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.lllbllllb.productinfoservice.model.Status;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.PRODUCT_CODE_BUILD_NUMBER_URL;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductInfoServiceGetByCodeAndBuildHappyPathFunctionalTest extends BaseFunctionalTest {

    @Test
    void shouldReturnStatus() throws Exception {
        // given
        // active round
        var firstRound = TestUtil.getRound("firstRound");
        var secondRound = TestUtil.getRound("secondRound");
        var firstBid = TestUtil.getBuildInfo("firstProduct", firstRound.getId(), Status.IN_PROGRESS);
        var secondBid = TestUtil.getBuildInfo("secondProduct", firstRound.getId(), Status.IN_PROGRESS);
        var thirdBid = TestUtil.getBuildInfo("thirdProduct", firstRound.getId(), Status.FINISHED);
        var fourthBid = TestUtil.getBuildInfo("fourthProduct", firstRound.getId(), Status.INVALID_CHECKSUM);
        // finished round
        var fifthBid = TestUtil.getBuildInfo("fifthProduct", secondRound.getId(), Status.FINISHED);
        fifthBid.setProductCode("TEST");
        var sixthBid = TestUtil.getBuildInfo("sixthProduct", secondRound.getId(), Status.FINISHED);
        sixthBid.setProductCode("TEST");
        var seventhBid = TestUtil.getBuildInfo("seventhProduct", secondRound.getId(), Status.FINISHED);
        var eighthBid = TestUtil.getBuildInfo("eighthProduct", secondRound.getId(), Status.INVALID_CHECKSUM);
        var thirdPid = TestUtil.getProductInfoDto(thirdBid.getId());
        var sixthPid = TestUtil.getProductInfoDto(sixthBid.getId());
        var seventhPid = TestUtil.getProductInfoDto(seventhBid.getId());
        var fifthPid = TestUtil.getProductInfoDto(fifthBid.getId());
        var delay = Duration.ZERO;
        roundRepository.saveAll(Flux.just(firstRound, secondRound).delayElements(delay)).collectList().block();
        buildInfoRepository.saveAll(Flux.just(firstBid, secondBid, thirdBid, fourthBid, fifthBid, sixthBid, seventhBid, eighthBid).delayElements(delay)).collectList().block();
        productInfoRepository.saveAll(Flux.just(thirdPid, fifthPid, sixthPid, seventhPid).delayElements(delay)).collectList().block();

        // when
        var actual = given()
            .when()
            .get(PRODUCT_CODE_BUILD_NUMBER_URL, sixthBid.getProductCode(), sixthBid.getFullNumber())
            .then()
            .statusCode(200)
            .and().log().all()
            .extract().body().as(JsonNode.class);
        var expected = objectMapper.readTree(sixthPid.getProductInfo().asArray());

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturn404() {
        given()
            .when()
            .get(PRODUCT_CODE_BUILD_NUMBER_URL, "IU", "42")
            .then()
            .statusCode(404);
    }
}
