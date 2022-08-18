package com.lllbllllb.productinfoservice;

import java.util.Collection;

import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <strong>Core</strong> API for to rule of the hole Product Info Service.
 */
public interface ProductInfoServiceCoreApiService {

    /**
     * Get the data about the most fresh and successfully processed builds.
     *
     * @return {@link Collection} of these builds
     */
    Mono<Collection<BuildInfoAware<Round>>> getLastBuildInfos();

    /**
     * Get the data about the builds from currently active {@link Round}s.
     *
     * @return {@link Collection} of these builds
     */
    Flux<BuildInfoAware<Pair<Status, Round>>> getActiveRoundsData();

    /**
     * Find all processed {@code product-info.json} by provided product code.
     *
     * @param productCode product code
     * @return founded {@link ProductInfo}
     */
    Flux<ProductInfo> findProductInfoByCode(String productCode);

    /**
     * Find unique {@code product-info.json} by provided product code and full build number.
     *
     * @param productCode product code
     * @param fullNumber  full build number
     * @return founded {@link ProductInfo}
     */
    Mono<ProductInfo> findProductInfoByCodeAndNumber(String productCode, String fullNumber);

    /**
     * Run the scanning process for all products.
     *
     * @return {@link BuildInfoAware} of just the builds found for update
     */
    Flux<BuildInfoAware<Round>> refreshAll();

    /**
     * Run the scanning process for provided product.
     *
     * @param productCode product code for update
     * @return {@link BuildInfoAware} of just the builds found for update
     */
    Flux<BuildInfoAware<Round>> refreshByCode(String productCode);
}
