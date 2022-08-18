package com.lllbllllb.productinfoservice.core;

import java.time.Duration;
import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceBuildInfoRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.lllbllllb.productinfoservice.model.Status.FAILED_DOWNLOAD;
import static com.lllbllllb.productinfoservice.model.Status.FINISHED;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ProductInfoServiceCoreBuildInfoService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceCoreBuildInfoServiceTest {

    @Mock
    private ProductInfoServiceCoreChecksumService checksumService;

    @Mock
    private ProductInfoServiceBuildInfoRepositoryService buildInfoRepositoryService;

    @Mock
    private ProductInfoServiceCoreRandomService randomService;

    @Mock
    private ProductInfoServiceCoreConfigurationProperties properties;

    @InjectMocks
    private ProductInfoServiceCoreBuildInfoService service;

    @Test
    void shouldFilterUnchangedBuildInfo() {
        // given
        var cs1 = "1";
        var cs2 = "2";
        var cs3 = "3";
        var cs4 = "4";
        var newBi1 = new BuildInfo("", 1, "", null, cs1);
        var newBi2 = new BuildInfo("", 2, "", null, cs2);
        var newBi3 = new BuildInfo("", 3, "", null, cs3);
        var newBi4 = new BuildInfo("", 4, "", null, cs4);
        var biMock1 = mock(BuildInfo.class);
        var cs5 = "5";

        // when
        when(randomService.shuffle(anyCollection()))
            .thenReturn(List.of(newBi1, newBi2, newBi3, newBi4));
        when(properties.getConcurrentCourtesyPeriod())
            .thenReturn(Duration.ofMillis(10));
        when(buildInfoRepositoryService.findBuildInfo(newBi1))
            .thenReturn(Mono.just(new BuildInfoAware<>(new BuildInfo("", 1, "", null, cs1), FAILED_DOWNLOAD)));
        when(buildInfoRepositoryService.findBuildInfo(newBi2))
            .thenReturn(Mono.just(new BuildInfoAware<>(new BuildInfo("", 2, "", null, cs2), FINISHED)));
        when(buildInfoRepositoryService.findBuildInfo(newBi3))
            .thenReturn(Mono.just(new BuildInfoAware<>(new BuildInfo("", 5, "", null, cs5), FINISHED)));
        when(buildInfoRepositoryService.findBuildInfo(newBi4))
            .thenReturn(Mono.empty());
        when(checksumService.isChecksumTheSame(cs2, cs2))
            .thenReturn(true);
        when(checksumService.isChecksumTheSame(cs5, cs3))
            .thenReturn(false);

        // then
        StepVerifier.create(service.filterBuildInfosToProceed(List.of()))
            .expectNext(newBi1)
            .expectNext(newBi3)
            .expectNext(newBi4)
            .verifyComplete();

    }
}
