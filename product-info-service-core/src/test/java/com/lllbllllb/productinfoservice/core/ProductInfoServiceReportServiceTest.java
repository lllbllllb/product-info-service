package com.lllbllllb.productinfoservice.core;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;

import com.lllbllllb.productinfoservice.ProductInfoServiceBuildInfoRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.model.Round;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ProductInfoServiceReportService}.
 */
@ExtendWith(MockitoExtension.class)
class ProductInfoServiceReportServiceTest {

    @Mock
    private ProductInfoServiceBuildInfoRepositoryService buildInfoRepositoryService;

    @Mock
    private Clock clock;

    @Mock
    private ProductInfoServiceCoreConfigurationProperties properties;

    @InjectMocks
    private ProductInfoServiceReportService service;

    @Test
    void shouldGetLastReleasedBuildInfos() {
        // given
        var from = Instant.ofEpochSecond(60);
        var period = Period.ofDays(1);
        var to = Instant.ofEpochSecond(60 + 24 * 60 * 60);
        var round = new Round("", Instant.ofEpochSecond(60));
        var metaIU1 = new BuildMetadata("", "", "", "", LocalDate.MIN, "", "IU");
        var metaIU2 = new BuildMetadata("", "", "", "", LocalDate.MAX, "", "IU");
        var metaGW = new BuildMetadata("", "", "", "", null, "", "GW");
        var bia1 = new BuildInfoAware<>(new BuildInfo("", 0, "", metaIU1, ""), round);
        var bia2 = new BuildInfoAware<>(new BuildInfo("", 0, "", metaIU2, ""), round);
        var bia3 = new BuildInfoAware<>(new BuildInfo("", 0, "", metaGW, ""), round);
        var flux = Flux.just(bia1, bia2, bia3);

        // when
        when(clock.instant())
            .thenReturn(to);
        when(clock.getZone())
            .thenReturn(ZoneId.systemDefault());
        when(properties.getReportPeriod())
            .thenReturn(period);
        when(buildInfoRepositoryService.findAllFinishedBuildsByPeriod(from, to))
            .thenReturn(flux);

        // then
        StepVerifier.create(service.getLastReleasedBuildInfos())
            .consumeNextWith(builds -> assertThat(builds, containsInAnyOrder(bia2, bia3)))
            .verifyComplete();
    }
}
