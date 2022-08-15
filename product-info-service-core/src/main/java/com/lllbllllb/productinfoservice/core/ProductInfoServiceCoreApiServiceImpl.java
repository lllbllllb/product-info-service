package com.lllbllllb.productinfoservice.core;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryLocalService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreApiServiceImpl implements ProductInfoServiceCoreApiService {

    private final ProductInfoServiceCoreMainEtlPipelineService mainFlowService;

    private final ProductInfoServiceRepositoryLocalService repositoryService;

    @Override
    public Mono<ServiceStatus> getServiceStatus() {
        return Mono.empty();
    }

    @Override
    public Flux<ProductInfo> findProductInfoByCode(String productCode) {
        return repositoryService.findProductInfoByProductCode(productCode);
    }

    @Override
    public Mono<ProductInfo> findProductInfoByCodeAndNumber(String productCode, String fullNumber) {
        return repositoryService.findProductInfoByProductCodeAndFullNumber(productCode, fullNumber);
    }

    @Override
    public Mono<List<BuildInfo>> refreshAll() {
        return mainFlowService.collect();
    }

    @Override
    public Mono<List<BuildInfo>> refreshByCode(String productCode) {
        return mainFlowService.collect(productCode);
    }
}
