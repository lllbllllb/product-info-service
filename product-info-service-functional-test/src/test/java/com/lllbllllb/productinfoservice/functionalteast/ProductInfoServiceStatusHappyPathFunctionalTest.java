package com.lllbllllb.productinfoservice.functionalteast;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lllbllllb.productinfoservice.controller.model.ActiveRoundDataDto;
import com.lllbllllb.productinfoservice.controller.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.controller.model.FullStatusDto;
import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repositorylocal.model.RoundDto;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.STATUS_URL;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductInfoServiceStatusHappyPathFunctionalTest extends BaseFunctionalTest {

    @Test
    void shouldReturnStatus() {
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
        var delay = Duration.ofSeconds(2);
        var savedRounds = roundRepository.saveAll(Flux.just(firstRound, secondRound).delayElements(delay)).collectList().block()
            .stream()
            .collect(Collectors.toMap(RoundDto::getId, Function.identity()));
        buildInfoRepository.saveAll(Flux.just(firstBid, secondBid, thirdBid, fourthBid, fifthBid, sixthBid, seventhBid, eighthBid).delayElements(delay)).collectList().block();
        productInfoRepository.saveAll(Flux.just(thirdPid, fifthPid, sixthPid, seventhPid).delayElements(delay)).collectList().block();

        // when
        var actual = given()
            .when()
            .get(STATUS_URL)
            .then()
            .statusCode(200)
            .and().log().all()
            .extract().body().as(FullStatusDto.class);
        var expected = new FullStatusDto(
            List.of(
                new ActiveRoundDataDto(firstBid.getProductCode(), firstBid.getProductName(), firstBid.getBuildVersion(), firstBid.getFullNumber(), firstBid.getStatus(), firstBid.getLink(), firstBid.getReleaseDate(), formatInstant(savedRounds.get(firstRound.getId()).getCreatedDate()), firstRound.getInstanceId()),
                new ActiveRoundDataDto(secondBid.getProductCode(), secondBid.getProductName(), secondBid.getBuildVersion(), secondBid.getFullNumber(), secondBid.getStatus(), secondBid.getLink(), secondBid.getReleaseDate(), formatInstant(savedRounds.get(firstRound.getId()).getCreatedDate()), firstRound.getInstanceId()),
                new ActiveRoundDataDto(thirdBid.getProductCode(), thirdBid.getProductName(), thirdBid.getBuildVersion(), thirdBid.getFullNumber(), thirdBid.getStatus(), thirdBid.getLink(), thirdBid.getReleaseDate(), formatInstant(savedRounds.get(firstRound.getId()).getCreatedDate()), firstRound.getInstanceId()),
                new ActiveRoundDataDto(fourthBid.getProductCode(), fourthBid.getProductName(), fourthBid.getBuildVersion(), fourthBid.getFullNumber(), fourthBid.getStatus(), fourthBid.getLink(), fourthBid.getReleaseDate(), formatInstant(savedRounds.get(firstRound.getId()).getCreatedDate()), firstRound.getInstanceId())
            ),
            List.of(
                new BuildInfoDto(seventhBid.getLink(), seventhBid.getChecksum(), seventhBid.getProductName(), seventhBid.getBuildVersion(), seventhBid.getReleaseDate(), seventhBid.getFullNumber(), seventhBid.getProductCode(), secondRound.getInstanceId(), formatInstant(secondRound.getCreatedDate())),
                new BuildInfoDto(thirdBid.getLink(), thirdBid.getChecksum(), thirdBid.getProductName(), thirdBid.getBuildVersion(), thirdBid.getReleaseDate(), thirdBid.getFullNumber(), thirdBid.getProductCode(), firstRound.getInstanceId(), formatInstant(firstRound.getCreatedDate())),
                new BuildInfoDto(fifthBid.getLink(), fifthBid.getChecksum(), fifthBid.getProductName(), fifthBid.getBuildVersion(), fifthBid.getReleaseDate(), fifthBid.getFullNumber(), fifthBid.getProductCode(), secondRound.getInstanceId(), formatInstant(secondRound.getCreatedDate()))
            )
        );

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnEmptyStatus() {
        var actual = given()
            .when()
            .get(STATUS_URL)
            .then()
            .statusCode(200)
            .and().log().all()
            .extract().body().as(FullStatusDto.class);
        var expected = new FullStatusDto(List.of(), List.of());

        assertEquals(expected, actual);
    }

    private String formatInstant(Instant instant) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC));
    }
}
