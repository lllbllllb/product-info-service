package com.lllbllllb.productinfoservice.core.handler;

import java.util.List;

import com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreMainFlowService;
import com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreStatusService;
import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import com.lllbllllb.productinfoservice.core.model.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
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

    private final ProductInfoServiceCoreMainFlowService mainFlowService;

    private final ProductInfoServiceCoreStatusService statusService;

    public Mono<ServerResponse> root(ServerRequest request) {

        return ok().bodyValue("Hello from root!");
    }

    public Mono<ServerResponse> getStatus(ServerRequest request) {
        return ok().body(statusService.getServiceStatus(), ServiceStatus.class);
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
        var tr = new ParameterizedTypeReference<List<BuildInfo>>() {
        };

        return ok().body(mainFlowService.collect(), tr);
    }

    public Mono<ServerResponse> refreshByCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);
        var tr = new ParameterizedTypeReference<List<BuildInfo>>() {
        };

        return ok().body(mainFlowService.collect(productCode), tr);
    }
}
