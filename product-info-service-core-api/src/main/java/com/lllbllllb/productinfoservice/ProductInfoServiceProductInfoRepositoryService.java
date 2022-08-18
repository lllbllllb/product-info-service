package com.lllbllllb.productinfoservice;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * API for {@link ProductInfo} repository service.
 */
public interface ProductInfoServiceProductInfoRepositoryService {

    /**
     * Save just mined product-info.json.
     *
     * @param buildInfo   {@link BuildInfo} as context
     * @param productInfo file for save
     * @return {@link BuildInfoAware} of the {@link ProductInfo}
     */
    Mono<BuildInfoAware<ProductInfo>> saveProductInfo(BuildInfo buildInfo, byte[] productInfo);

    /**
     * Find {@link ProductInfo} by the product code.
     *
     * @param productCode the product code
     * @return {@link Flux} of the founded {@link ProductInfo}s
     */
    Flux<ProductInfo> findProductInfoByProductCode(String productCode);

    /**
     * Find unique {@link ProductInfo} by the provided product code and build number.
     *
     * @param productCode the product code
     * @param fullNumber  full build number
     * @return founded {@link ProductInfo}
     */
    Mono<ProductInfo> findProductInfoByProductCodeAndFullNumber(String productCode, String fullNumber);
}
