package com.lllbllllb.productinfoservice.core;

import java.nio.file.Path;
import java.time.Instant;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ProductInfoServiceCoreChecksumService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceCoreChecksumServiceTest {

    @Mock
    private ProductInfoServiceCoreFinalizeService finalizeService;

    @InjectMocks
    private ProductInfoServiceCoreChecksumService service;

    @Test
    void shouldReturnTrue() {
        // given
        var expected = "42";
        var actual = "42";

        // when
        var res = service.isChecksumTheSame(expected, actual);

        // then
        assertTrue(res);
    }

    @NullSource
    @ValueSource(strings = {"", "  ", "4"})
    @ParameterizedTest
    void shouldReturnFalse(String actual) {
        // given
        var expected = "42";

        // when
        var res = service.isChecksumTheSame(expected, actual);

        // then
        assertFalse(res);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldGetActualChecksum() throws Exception {
        // given
        var expectedSha256 = "7534602cad0eed5318cd8f061975e425d71def989323a2597be7240c68b344de";
        var path = Path.of(this.getClass().getClassLoader().getResource("stubs/intellij-community-idea-221.6008.13.tar.gz").toURI());

        // when
        // then
        StepVerifier.create(service.getActualChecksum(path))
            .expectNext(expectedSha256)
            .verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldPermitChecksum() throws Exception {
        // given
        var expectedSha256 = "7534602cad0eed5318cd8f061975e425d71def989323a2597be7240c68b344de";
        var path = Path.of(this.getClass().getClassLoader().getResource("stubs/intellij-community-idea-221.6008.13.tar.gz").toURI());
        var round = new Round("", Instant.ofEpochSecond(60));
        var buildInfo = new BuildInfo("", 0, "", null, expectedSha256);

        //when
        //then
        StepVerifier.create(service.validateFileChecksum(buildInfo, path, round))
            .expectNext(new BuildInfoAware<>(buildInfo, path))
            .verifyComplete();

        verifyNoInteractions(finalizeService);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void shouldRejectChecksum() throws Exception {
        // given
        var expectedSha256 = "42";
        var path = Path.of(this.getClass().getClassLoader().getResource("stubs/intellij-community-idea-221.6008.13.tar.gz").toURI());
        var round = new Round("", Instant.ofEpochSecond(60));
        var buildInfo = new BuildInfo("", 0, "", null, expectedSha256);

        //when
        when(finalizeService.finalize(buildInfo, Status.INVALID_CHECKSUM, round))
            .thenReturn(Mono.empty());

        //then
        assertAll(
            () -> StepVerifier.create(service.validateFileChecksum(buildInfo, path, round))
                .verifyComplete(),
            () -> verifyNoMoreInteractions(finalizeService)
        );
    }
}
