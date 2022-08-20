package com.lllbllllb.productinfoservice;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import reactor.core.publisher.Flux;

/**
 * API for {@link BuildInfo} foreign repository service.
 */
public interface ProductInfoServiceRepositoryRemoteService {

    /**
     * Get all {@link BuildInfo}s from provider for processing.
     *
     * @return {@link Flux} of the provided {@link BuildInfo}s
     */
    Flux<BuildInfo> getAllBuildInfo();

    /**
     * Get {@link BuildInfo}s from provider by product code for processing.
     *
     * @return {@link Flux} of the provided {@link BuildInfo}s
     */
    Flux<BuildInfo> getBuildInfoByProductCode(String productCode);

    void rollbackLastCheck();
}
