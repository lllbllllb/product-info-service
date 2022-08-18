package com.lllbllllb.productinfoservice.controller;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.controller.model.ActiveRoundDataDto;
import com.lllbllllb.productinfoservice.controller.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.controller.model.FullStatusDto;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.STATUS_URL;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ProductInfoServiceControllerHttpRequestHandler}.
 */
@ExtendWith(SpringExtension.class)
@WebFluxTest
@ContextConfiguration(classes = ProductInfoServiceControllerAutoConfiguration.class)
class ProductInfoServiceControllerHttpRequestHandlerTest {


    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ProductInfoServiceCoreApiService apiService;

    @MockBean
    private ProductInfoServiceControllerConverterService converterService;

    @Test
    void shouldGetStatus() throws Exception {
        // given
        var biaPair1 = new BuildInfoAware<>(null, Pair.of(Status.IN_PROGRESS, new Round("1", Instant.ofEpochSecond(1))));
        var biaPair2 = new BuildInfoAware<>(null, Pair.of(Status.IN_PROGRESS, new Round("2", Instant.ofEpochSecond(2))));
        var ardd1 = new ActiveRoundDataDto("3", null, null, null, null, null, null, null, null);
        var ardd2 = new ActiveRoundDataDto("4", null, null, null, null, null, null, null, null);
        var biaRound1 = new BuildInfoAware<>(null, new Round("5", null));
        var biaRound2 = new BuildInfoAware<>(null, new Round("6", null));
        var bid1 = new BuildInfoDto("7", null, null, null, null, null, null, null, null);
        var bid2 = new BuildInfoDto("7", null, null, null, null, null, null, null, null);

        // when
        when(apiService.getActiveRoundsData())
            .thenReturn(Flux.just(biaPair1, biaPair2));
        when(converterService.toActiveRoundDataDto(biaPair1))
            .thenReturn(ardd1);
        when(converterService.toActiveRoundDataDto(biaPair2))
            .thenReturn(ardd2);
        when(apiService.getLastBuildInfos())
            .thenReturn(Mono.just(List.of(biaRound1, biaRound2)));
        when(converterService.toBuildInfoDto(biaRound1))
            .thenReturn(bid1);
        when(converterService.toBuildInfoDto(biaRound2))
            .thenReturn(bid2);

        // then
        webClient
            .get().uri(STATUS_URL)
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody().json(mapper.writeValueAsString(new FullStatusDto(List.of(ardd1, ardd2), List.of(bid1, bid2))), true);
    }
}
