package com.lllbllllb.productinfoservice;

import com.lllbllllb.productinfoservice.model.Round;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceRoundRepositoryService {

    Mono<Round> saveRound(Round round);
}
