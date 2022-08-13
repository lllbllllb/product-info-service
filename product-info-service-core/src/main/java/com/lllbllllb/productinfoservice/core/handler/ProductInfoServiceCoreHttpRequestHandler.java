package com.lllbllllb.productinfoservice.core.handler;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreMainFlowService;
import com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreStatusService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.ServiceStatus;
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

    private final ProductInfoServiceRepositoryService repositoryService;

    public Mono<ServerResponse> root(ServerRequest request) {

        return ok().bodyValue("Hello from root!");
    }

    public Mono<ServerResponse> getStatus(ServerRequest request) {
        return ok().body(statusService.getServiceStatus(), ServiceStatus.class);
    }

    public Mono<ServerResponse> getProductCode(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);

        return repositoryService.findByProductCode(productCode)
            .map(bia -> bia.obj().productInfoFile())
            .collectList()
            .flatMap(file -> ok().bodyValue(file));
    }

    public Mono<ServerResponse> getBuildNumber(ServerRequest request) {
        var productCode = request.pathVariable(PRODUCT_CODE);
        var buildNumber = request.pathVariable(BUILD_NUMBER);

        return repositoryService.findByProductCodeAndFullNumber(productCode, buildNumber)
            .flatMap(bia -> ok().bodyValue(bia.obj().productInfoFile()));
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
