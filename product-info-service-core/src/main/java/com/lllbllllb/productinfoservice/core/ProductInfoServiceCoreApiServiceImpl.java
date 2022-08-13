package com.lllbllllb.productinfoservice.core;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreApiServiceImpl implements ProductInfoServiceCoreApiService {

    private final ProductInfoServiceCoreMainFlowService mainFlowService;

    private final ProductInfoServiceCoreStatusService statusService;

    private final ProductInfoServiceRepositoryService repositoryService;

    @Override
    public Mono<ServiceStatus> getServiceStatus() {
        return statusService.getServiceStatus();
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
