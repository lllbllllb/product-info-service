package com.lllbllllb.productinfoservice;

import com.lllbllllb.productinfoservice.model.Round;
import reactor.core.publisher.Mono;

/**
 * API for {@link Round} repository service.
 */
public interface ProductInfoServiceRoundRepositoryService {

    /**
     * Save data about another one {@link Round} or the builds scanning.
     *
     * @param round {@link Round} to save
     * @return saved {@link Round}
     */
    Mono<Round> saveRound(Round round);
}
