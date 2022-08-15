package com.lllbllllb.productinfoservice.core;

import java.util.List;
import java.util.Map;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreApiServiceImpl implements ProductInfoServiceCoreApiService {

    private final ProductInfoServiceCoreEtlPipelineService mainFlowService;

    private final ProductInfoServiceCoreProductInfoService repositoryService;

    private final ProductInfoServiceReportService reportService;

    @Override
    public Mono<Map<String, BuildInfoAware<Round>>> getLastBuildInfos() {
        return reportService.getLastReleasedBuildInfos();
    }

    @Override
    public Flux<ProductInfo> findProductInfoByCode(String productCode) {
        return repositoryService.findByCode(productCode);
    }

    @Override
    public Mono<ProductInfo> findProductInfoByCodeAndNumber(String productCode, String fullNumber) {
        return repositoryService.findByCodeAndNumber(productCode, fullNumber);
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
