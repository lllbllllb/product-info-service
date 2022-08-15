package com.lllbllllb.productinfoservice.core;

import java.nio.file.Files;
import java.nio.file.Path;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreChecksumService {

    private final ProductInfoServiceCoreFinalizeService finalizeService;

    public Mono<String> getActualChecksum(Path path) {
        return Mono.fromCallable(() -> {
                try (var is = Files.newInputStream(path)) {
                    return DigestUtils.sha256Hex(is);
                }
            })
            .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<BuildInfoAware<Path>> validateFileChecksum(BuildInfo buildInfo, Path path) {
        return getActualChecksum(path)
            .handle((actualChecksum, sink) -> {
                var expectedChecksum = buildInfo.checksum();

                if (isChecksumTheSame(expectedChecksum, actualChecksum)) {
                    sink.next(new BuildInfoAware<>(buildInfo, path));
                } else {
                    finalizeService.finalize(buildInfo, Status.INVALID_CHECKSUM)
                        .subscribe();
                }
            });
    }

    public boolean isChecksumTheSame(String sha256Expected, String sha256Actual) {
        return StringUtils.hasLength(sha256Expected)
            && StringUtils.hasLength(sha256Actual)
            && sha256Expected.equalsIgnoreCase(sha256Actual);
    }

}
