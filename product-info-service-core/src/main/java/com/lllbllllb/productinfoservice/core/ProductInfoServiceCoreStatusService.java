package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.core.model.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreStatusService {

    private final ProductInfoServiceCoreBuildDownloadService downloadService;

    public Mono<ServiceStatus> getServiceStatus() {
        return Mono.fromCallable(() -> new ServiceStatus(downloadService.getProgressMap()));
    }
}
