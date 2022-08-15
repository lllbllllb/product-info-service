package com.lllbllllb.productinfoservice;

import java.util.List;
import java.util.Map;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceCoreApiService {

    Mono<Map<String, BuildInfoAware<Round>>> getLastBuildInfos();

    Flux<ProductInfo> findProductInfoByCode(String productCode);

    Mono<ProductInfo> findProductInfoByCodeAndNumber(String productCode, String fullNumber);

    Mono<List<BuildInfo>> refreshAll();

    Mono<List<BuildInfo>> refreshByCode(String productCode);
}
