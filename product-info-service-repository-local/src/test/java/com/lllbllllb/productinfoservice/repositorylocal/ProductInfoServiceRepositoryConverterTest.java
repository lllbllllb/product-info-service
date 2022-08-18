package com.lllbllllb.productinfoservice.repositorylocal;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repositorylocal.model.BuildInfoDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ProductInfoServiceRepositoryConverter}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceRepositoryConverterTest {

    @Mock
    private ProductInfoServiceRepositoryIdProvider idProvider;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductInfoServiceRepositoryConverter converter;

    @Test
    void shouldConvertToBuildInfoDto() {
        // given
        var meta = new BuildMetadata(
            "Name42",
            "Channel42",
            "cStatus42",
            "version42",
            LocalDate.of(1990, 1, 1),
            "number42",
            "code42"
        );
        var bi = new BuildInfo(
            "www3",
            42,
            "www3.sha256",
            meta,
            "4815162342"
        );
        var status = Status.FINISHED;
        var roundId = UUID.randomUUID();
        var biId = UUID.randomUUID();
        var expected = new BuildInfoDto();
        expected.setId(biId);
        expected.setProductCode("code42");
        expected.setChecksum("4815162342");
        expected.setChecksumLink("www3.sha256");
        expected.setLink("www3");
        expected.setSize(42);
        expected.setChannelName("Channel42");
        expected.setChannelStatus("cStatus42");
        expected.setProductName("Name42");
        expected.setBuildVersion("version42");
        expected.setReleaseDate(LocalDate.of(1990, 1, 1));
        expected.setFullNumber("number42");
        expected.setStatus(status);
        expected.setRoundId(roundId);

        // when
        when(idProvider.getBuildInfoId(bi))
            .thenReturn(biId);

        var res = converter.toDto(bi, status, roundId);

        assertEquals(expected, res);
    }

    @Test
    void shouldConvertBuildInfoAwareTFromDto() {
        // given
        var status = Status.FINISHED;
        var biId = UUID.randomUUID();
        var dto = new BuildInfoDto();
        dto.setId(biId);
        dto.setProductCode("code42");
        dto.setChecksum("4815162342");
        dto.setChecksumLink("www3.sha256");
        dto.setLink("www3");
        dto.setSize(42);
        dto.setChannelName("Channel42");
        dto.setChannelStatus("cStatus42");
        dto.setProductName("Name42");
        dto.setBuildVersion("version42");
        dto.setReleaseDate(LocalDate.of(1990, 1, 1));
        dto.setFullNumber("number42");
        dto.setStatus(status);
        var metaExpected = new BuildMetadata(
            "Name42",
            "Channel42",
            "cStatus42",
            "version42",
            LocalDate.of(1990, 1, 1),
            "number42",
            "code42"
        );
        var biExpected = new BuildInfo(
            "www3",
            42,
            "www3.sha256",
            metaExpected,
            "4815162342"
        );
        var biaExpected = new BuildInfoAware<>(biExpected, status);

        // when
        var res = converter.fromDto(dto, status);

        // then
        assertEquals(biaExpected, res);
    }

    @Test
    void testFromDto() {
    }

    @Test
    void testFromDto1() {
    }

    @Test
    void toBuildInfoJson() {
    }

    @Test
    void testToDto1() {
    }

    @Test
    void testFromDto2() {
    }
}
