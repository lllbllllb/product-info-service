package com.lllbllllb.productinfoservice;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceProductInfoRepositoryService {

    Mono<BuildInfoAware<ProductInfo>> saveProductInfo(BuildInfo buildInfo, byte[] productInfo);

    Flux<ProductInfo> findProductInfoByProductCode(String productCode);

    Mono<ProductInfo> findProductInfoByProductCodeAndFullNumber(String productCode, String fullNumber);
}
