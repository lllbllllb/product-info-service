package com.lllbllllb.productinfoservice.core;

import java.util.Collection;

import com.lllbllllb.productinfoservice.ProductInfoServiceCoreApiService;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
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
    public Mono<Collection<BuildInfoAware<Round>>> getLastBuildInfos() {
        return reportService.getLastReleasedBuildInfos();
    }

    @Override
    public Flux<BuildInfoAware<Pair<Status, Round>>> getActiveRoundsData() {
        return reportService.getActiveRoundData();
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
    public Flux<BuildInfoAware<Round>> refreshAll() {
        return mainFlowService.startRound();
    }

    @Override
    public Flux<BuildInfoAware<Round>> refreshByCode(String productCode) {
        return mainFlowService.startRound(productCode);
    }
}
