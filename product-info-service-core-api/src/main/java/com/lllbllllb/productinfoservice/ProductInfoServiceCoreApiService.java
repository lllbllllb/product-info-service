package com.lllbllllb.productinfoservice;

import java.util.Collection;
import java.util.List;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceCoreApiService {

    Mono<Collection<BuildInfoAware<Round>>> getLastBuildInfos();

    Flux<BuildInfoAware<Pair<Status, Round>>> getActiveRoundsData();

    Flux<ProductInfo> findProductInfoByCode(String productCode);

    Mono<ProductInfo> findProductInfoByCodeAndNumber(String productCode, String fullNumber);

    Mono<List<BuildInfo>> refreshAll();

    Mono<List<BuildInfo>> refreshByCode(String productCode);
}
