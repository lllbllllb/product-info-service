package com.lllbllllb.productinfoservice.core;

import java.util.Optional;
import java.util.logging.Level;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFailureService {

    private final ProductInfoServiceRepositoryService repositoryService;

    public void handleFail(Throwable ex, Object obj) {
        extractBuildInfo(obj).ifPresentOrElse(
            bi -> fireAndForget(repositoryService.saveBuildInfo(bi, Status.FAILED), ex),
            () -> fireAndForget(Mono.just(ex), ex)
        );
    }

    private void fireAndForget(Mono<?> publisher, Throwable ex) {
        publisher
            .log(ex.getMessage(), Level.SEVERE)
            .subscribe();
    }

    private Optional<BuildInfo> extractBuildInfo(Object obj) {
        var parameterType = obj.getClass();

        if (BuildInfoAware.class.equals(parameterType)) {
            return Optional.of(((BuildInfoAware<?>) obj).buildInfo());
        }

        if (BuildInfo.class.equals(parameterType)) {
            return Optional.of((BuildInfo) obj);
        }

        return Optional.empty();
    }
}
