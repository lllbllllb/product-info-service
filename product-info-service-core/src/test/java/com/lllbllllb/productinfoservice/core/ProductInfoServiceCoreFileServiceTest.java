package com.lllbllllb.productinfoservice.core;

import java.time.Instant;
import java.time.LocalDate;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.model.Round;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link ProductInfoServiceCoreFileService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceCoreFileServiceTest {

    @Mock
    private ProductInfoServiceCoreConfigurationProperties properties;

    @InjectMocks
    private ProductInfoServiceCoreFileService service;

    @Test
    void shouldReturnUniqNames() {
        // given
        var meta = new BuildMetadata("i", "c", "r", "v1", LocalDate.of(1990, 1, 1), "42", "IU");
        var bi = new BuildInfo("www", 10, "www3", meta, "4815162342");
        var round = new Round("ri", Instant.ofEpochSecond(1));
        var expected = "i_IU_1990-01-01_42_4815162342_ri.tar.gz";

        // when
        var res = service.getName(bi, round);

        // then
        assertEquals(expected, res);
    }
}
