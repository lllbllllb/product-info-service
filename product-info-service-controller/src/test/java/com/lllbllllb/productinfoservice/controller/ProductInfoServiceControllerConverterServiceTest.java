package com.lllbllllb.productinfoservice.controller;

import java.time.Instant;
import java.time.LocalDate;

import com.lllbllllb.productinfoservice.controller.model.ActiveRoundDataDto;
import com.lllbllllb.productinfoservice.controller.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link ProductInfoServiceControllerConverterService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceControllerConverterServiceTest {

    @InjectMocks
    private ProductInfoServiceControllerConverterService converter;

    @Test
    void shouldConvertToBuildInfoDto() {
        // given
        var metadata = new BuildMetadata(
            "productName42",
            "channelName42",
            "channelStatus42",
            "version42",
            LocalDate.of(1990, 3, 25),
            "fullNumber42",
            "productCode42"
        );
        var buildInfo = new BuildInfo(
            "link42",
            10,
            "checksumLink42",
            metadata,
            "checksum42"
        );
        var round = new Round("instanceId42", Instant.ofEpochSecond(60));
        var bia = new BuildInfoAware<>(buildInfo, round);
        var expected = new BuildInfoDto(
            "link42",
            "checksum42",
            "productName42",
            "version42",
            LocalDate.of(1990, 3, 25),
            "fullNumber42",
            "productCode42",
            "instanceId42",
            "1970-01-01 00:01:00"
        );

        // when
        var actual = converter.toBuildInfoDto(bia);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void shouldConvertToActiveRoundDataDto() {
        // given
        var metadata = new BuildMetadata(
            "productName42",
            "channelName42",
            "channelStatus42",
            "version42",
            LocalDate.of(1990, 3, 25),
            "fullNumber42",
            "productCode42"
        );
        var buildInfo = new BuildInfo(
            "link42",
            10,
            "checksumLink42",
            metadata,
            "checksum42"
        );
        var round = new Round("instanceId42", Instant.ofEpochSecond(60));
        var status = Status.IN_PROGRESS;
        var bia = new BuildInfoAware<>(buildInfo, Pair.of(status, round));
        var expected = new ActiveRoundDataDto(
            "productCode42",
            "productName42",
            "version42",
            "fullNumber42",
            Status.IN_PROGRESS,
            "link42",
            LocalDate.of(1990, 3, 25),
            "1970-01-01 00:01:00",
            "instanceId42"
        );

        // when
        var actual = converter.toActiveRoundDataDto(bia);

        // then
        assertEquals(expected, actual);
    }
}
