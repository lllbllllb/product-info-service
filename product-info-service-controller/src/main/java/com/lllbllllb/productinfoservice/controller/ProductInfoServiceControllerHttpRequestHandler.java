package com.lllbllllb.productinfoservice.controller;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.ServiceStatus;
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

    public Mono<ServerResponse> homepage(ServerRequest request) {

        return ok().bodyValue("Hello from root!");
    }

    public Mono<ServerResponse> getLastBuildInfos(ServerRequest request) {
        return ok().body(apiService.getLastBuildInfos(), ServiceStatus.class);
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
