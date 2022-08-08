package com.lllbllllb.productinfoservice.core;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreHttpRoutes.BUILD_NUMBER;
import static com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreHttpRoutes.PRODUCT_CODE;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreHttpRequestHandler {

    public Mono<ServerResponse> root(ServerRequest request) {

        return ok().bodyValue("Hello from root!");
    }

    public Mono<ServerResponse> getStatus(ServerRequest request) {
        return ok().bodyValue("Hello from status!");
    }

    public Mono<ServerResponse> getProductCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);

        return ok().bodyValue("Your PC: " + productCode);
    }

    public Mono<ServerResponse> getBuildNumber(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);
        var buildNumber = request.pathVariable(BUILD_NUMBER);

        return ok().bodyValue(String.format("PC: %s | BN: %s", productCode, buildNumber));
    }

    public Mono<ServerResponse> refresh(ServerRequest request) {
        return ok().bodyValue("Hello from refresh!");
    }

    public Mono<ServerResponse> refreshByCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);

        return ok().bodyValue("Refresh PC: " + productCode);
    }
}
