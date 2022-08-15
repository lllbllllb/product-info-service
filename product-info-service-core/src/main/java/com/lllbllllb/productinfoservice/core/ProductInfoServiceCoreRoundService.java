package com.lllbllllb.productinfoservice.core;

import java.time.Clock;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryLocalService;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreRoundService {

    private final ProductInfoServiceCoreInstanceService instanceService;

    private final ProductInfoServiceRepositoryLocalService repositoryLocalService;

    private final Clock clock;

    public Mono<Round> createRound() {
        var instanceId = instanceService.getInstanceId();
        var createdDate = clock.instant();
        var round = new Round(instanceId, createdDate);

        return repositoryLocalService.saveRound(round);
    }
}
