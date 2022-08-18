package com.lllbllllb.productinfoservice.repositorylocal;

import java.time.Instant;
import java.util.UUID;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.model.Round;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for {@link ProductInfoServiceRepositoryIdProvider}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceRepositoryIdProviderTest {

    @InjectMocks
    private ProductInfoServiceRepositoryIdProvider provider;

    @Test
    void shouldGetBuildInfoId() {
        // given
        var meta = new BuildMetadata("", "", "", "", null, "481516", "IU");
        var bi = new BuildInfo("", 1, "", meta, "42");
        var expected = UUID.nameUUIDFromBytes("IU_481516".getBytes());

        // when
        var res = provider.getBuildInfoId(bi);

        // then
        assertEquals(expected, res);
    }

    @Test
    void shouldGetRoundId() {
        // given
        var round = new Round("42", Instant.ofEpochSecond(60));
        var expected = UUID.nameUUIDFromBytes("42_1970-01-01T00:01:00Z".getBytes());

        // when
        var res = provider.getRoundId(round);

        // then
        assertEquals(expected, res);
    }
}
