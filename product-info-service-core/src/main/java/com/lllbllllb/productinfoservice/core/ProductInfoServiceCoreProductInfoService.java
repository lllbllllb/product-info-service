package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.ProductInfoServiceProductInfoRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreProductInfoService {

    private final ProductInfoServiceProductInfoRepositoryService productInfoRepositoryService;

    public Mono<BuildInfoAware<ProductInfo>> saveProductInfo(BuildInfo buildInfo, byte[] productInfo) {
        return productInfoRepositoryService.saveProductInfo(buildInfo, productInfo);
    }

    public Flux<ProductInfo> findByCode(String productCode) {
        return productInfoRepositoryService.findProductInfoByProductCode(productCode);
    }

    public Mono<ProductInfo> findByCodeAndNumber(String productCode, String fullNumber) {
        return productInfoRepositoryService.findProductInfoByProductCodeAndFullNumber(productCode, fullNumber);
    }
}
