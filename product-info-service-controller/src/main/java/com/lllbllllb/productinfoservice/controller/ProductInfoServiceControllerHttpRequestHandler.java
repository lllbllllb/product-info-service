package com.lllbllllb.productinfoservice.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.controller.model.FullStatusDto;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.BUILD_NUMBER;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.PRODUCT_CODE;
import static org.springframework.http.HttpHeaders.LAST_MODIFIED;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceControllerHttpRequestHandler {

    private final ProductInfoServiceCoreApiService apiService;

    private final ProductInfoServiceControllerConverterService converterService;

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
            .flatMap(dto -> ok().bodyValue(dto))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getLastBuildData(ServerRequest request) {
        return apiService.getLastBuildInfos()
            .map(bia -> bia.stream()
                .map(converterService::toBuildInfoDto)
                .collect(Collectors.toList()))
            .flatMap(dtos -> ok().bodyValue(dtos));
    }

    public Mono<ServerResponse> getActiveRoundsData(ServerRequest request) {
        return apiService.getActiveRoundsData()
            .sort(Comparator.comparing(buildInfoAware -> buildInfoAware.obj().getRight().cratedDate()))
            .map(converterService::toActiveRoundDataDto)
            .collectList()
            .flatMap(dtos -> ok().bodyValue(dtos));
    }

    public Mono<ServerResponse> getByProductCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);

        return apiService.findProductInfoByCode(productCode)
            .map(ProductInfo::productInfoFile)
            .collectList()
            .flatMap(file -> ok().bodyValue(file))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getByProductCodeAndBuildNumber(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);
        var buildNumber = request.pathVariable(BUILD_NUMBER);

        return apiService.findProductInfoByCodeAndNumber(productCode, buildNumber)
            .flatMap(productInfo -> ok()
                .header(LAST_MODIFIED, Long.toString(productInfo.updatedDate().toEpochMilli()))
                .bodyValue(productInfo.productInfoFile())
            )
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> refresh(ServerRequest request) {
        var tr = new ParameterizedTypeReference<List<BuildInfo>>() {
        };

        return ok().body(apiService.refreshAll(), tr);
    }

    public Mono<ServerResponse> refreshByCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);
        var tr = new ParameterizedTypeReference<List<BuildInfo>>() {
        };

        return ok().body(apiService.refreshByCode(productCode), tr);
    }
}
