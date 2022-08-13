package com.lllbllllb.productinfoservice;

import java.util.List;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceRepositoryService {

    Mono<BuildInfoAware<ProductInfo>> save(BuildInfoAware<byte[]> buildInfoAware);

    Flux<BuildInfoAware<ProductInfo>> findAllByBuildInfo(List<BuildInfo> buildInfos);

    Flux<BuildInfoAware<ProductInfo>> findByProductCode(String productCode);

    Mono<BuildInfoAware<ProductInfo>> findByProductCodeAndFullNumber(String productCode, String fullNumber);

}
