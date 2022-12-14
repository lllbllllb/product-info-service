package com.lllbllllb.productinfoservice.controller;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.controller.model.FullStatusDto;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.BUILD_NUMBER;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.PRODUCT_CODE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * Handler for functional routes.
 *
 * @see ProductInfoServiceControllerAutoConfiguration
 */
@Service
@RequiredArgsConstructor
public class ProductInfoServiceControllerHttpRequestHandler {

    private final ProductInfoServiceCoreApiService apiService;

    private final ProductInfoServiceControllerConverterService converterService;

    /**
     * Handler for {@code GET} to {@link ProductInfoServiceControllerHttpRoutes#STATUS_URL}.
     *
     * @param request {@link ServerRequest}
     * @return {@link Mono<ServerResponse>}
     */
    public Mono<ServerResponse> getStatus(ServerRequest request) {
        return apiService.getActiveRoundsData()
            .sort(Comparator.comparing(buildInfoAware -> buildInfoAware.obj().getRight().cratedDate()))
            .map(converterService::toActiveRoundDataDto)
            .collectList()
            .zipWith(apiService.getLastBuildInfos()
                .map(bia -> bia.stream()
                    .map(converterService::toBuildInfoDto)
                    .collect(Collectors.toList())))
            .map(tuple2 -> new FullStatusDto(tuple2.getT1(), tuple2.getT2()))
            .flatMap(dto -> ok().bodyValue(dto));
    }

    /**
     * Handler for {@code GET} to {@code /lastBuildsData}.
     *
     * @param request {@link ServerRequest}
     * @return {@link Mono<ServerResponse>}
     */
    public Mono<ServerResponse> getLastBuildData(ServerRequest request) {
        return apiService.getLastBuildInfos()
            .map(bia -> bia.stream()
                .map(converterService::toBuildInfoDto)
                .collect(Collectors.toList()))
            .flatMap(dtos -> ok().bodyValue(dtos));
    }

    /**
     * Handler for {@code GET} to {@code /activeRoundsData}.
     *
     * @param request {@link ServerRequest}
     * @return {@link Mono<ServerResponse>}
     */
    public Mono<ServerResponse> getActiveRoundsData(ServerRequest request) {
        return apiService.getActiveRoundsData()
            .sort(Comparator.comparing(buildInfoAware -> buildInfoAware.obj().getRight().cratedDate()))
            .map(converterService::toActiveRoundDataDto)
            .collectList()
            .flatMap(dtos -> ok().bodyValue(dtos));
    }

    /**
     * Handler for {@code GET} to {@link ProductInfoServiceControllerHttpRoutes#PRODUCT_CODE_URL}.
     *
     * @param request {@link ServerRequest}
     * @return {@link Mono<ServerResponse>}
     */
    public Mono<ServerResponse> getByProductCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);

        return apiService.findProductInfoByCode(productCode)
            .map(ProductInfo::productInfoFile)
            .collectList()
            .filter(res -> !CollectionUtils.isEmpty(res))
            .flatMap(file -> ok().bodyValue(file))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Handler for {@code GET} to {@link ProductInfoServiceControllerHttpRoutes#PRODUCT_CODE_BUILD_NUMBER_URL}.
     *
     * @param request {@link ServerRequest}
     * @return {@link Mono<ServerResponse>}
     */
    public Mono<ServerResponse> getByProductCodeAndBuildNumber(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);
        var buildNumber = request.pathVariable(BUILD_NUMBER);

        return apiService.findProductInfoByCodeAndNumber(productCode, buildNumber)
            .flatMap(productInfo -> ok().bodyValue(productInfo.productInfoFile()))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * Handler for {@code GET} to {@link ProductInfoServiceControllerHttpRoutes#REFRESH_URL}.
     *
     * @param request {@link ServerRequest}
     * @return {@link Mono<ServerResponse>}
     */
    public Mono<ServerResponse> refresh(ServerRequest request) {
        return apiService.refreshAll()
            .map(converterService::toBuildInfoDto)
            .collectList()
            .flatMap(dtos -> ok().bodyValue(dtos));
    }

    /**
     * Handler for {@code GET} to {@link ProductInfoServiceControllerHttpRoutes#REFRESH_PRODUCT_CODE_URL}.
     *
     * @param request {@link ServerRequest}
     * @return {@link Mono<ServerResponse>}
     */
    public Mono<ServerResponse> refreshByCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);

        return apiService.refreshByCode(productCode)
            .map(converterService::toBuildInfoDto)
            .collectList()
            .flatMap(dtos -> ok().bodyValue(dtos));
    }
}
