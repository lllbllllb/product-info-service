package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.ProductInfoServiceBuildInfoRepositoryService;
import com.lllbllllb.productinfoservice.core.model.CleanupPolicy;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ProductInfoServiceCoreFinalizeService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceCoreFinalizeServiceTest {

    @Mock
    private ProductInfoServiceCoreFileService fileService;

    @Mock
    private ProductInfoServiceCoreConfigurationProperties properties;

    @Mock
    private ProductInfoServiceBuildInfoRepositoryService buildInfoRepositoryService;

    @InjectMocks
    private ProductInfoServiceCoreFinalizeService service;

    @Test
    void shouldDeleteFile() {
        var bi = new BuildInfo("", 0, "", null, "");
        var status = Status.FINISHED;
        var round = mock(Round.class);

        // when
        when(buildInfoRepositoryService.updateBuildInfo(bi, status))
            .thenReturn(Mono.just(new BuildInfoAware<>(bi, status)));
        when(properties.getCleanupPolicy())
            .thenReturn(CleanupPolicy.ALL);
        when(fileService.deleteFile(bi, round))
            .thenReturn(Mono.just(true));

        // then
        StepVerifier.create(service.finalize(bi, status, round))
            .expectNext(new BuildInfoAware<>(bi, true))
            .verifyComplete();
    }

    @Test
    void shouldNotDeleteFile() {
        var bi = new BuildInfo("", 0, "", null, "");
        var status = Status.FINISHED;
        var round = mock(Round.class);

        // when
        when(buildInfoRepositoryService.updateBuildInfo(bi, status))
            .thenReturn(Mono.just(new BuildInfoAware<>(bi, status)));
        when(properties.getCleanupPolicy())
            .thenReturn(CleanupPolicy.NONE);

        // then
        StepVerifier.create(service.finalize(bi, status, round))
            .expectNext(new BuildInfoAware<>(bi, true))
            .verifyComplete();
        verifyNoInteractions(fileService);
    }
}
