package com.lllbllllb.productinfoservice;

import java.util.List;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Status;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceRepositoryService {

    Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Status status);

    Mono<ProductInfo> saveProductInfo(BuildInfoAware<byte[]> buildInfoAware);

    Flux<BuildInfoAware<Status>> findAllBuildInfo(List<BuildInfo> buildInfos);

    Flux<ProductInfo> findAllProductInfo(List<BuildInfo> buildInfos);

    Flux<ProductInfo> findProductInfoByProductCode(String productCode);

    Mono<ProductInfo> findProductInfoByProductCodeAndFullNumber(String productCode, String fullNumber);

}
