package com.lllbllllb.productinfoservice.core;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import com.lllbllllb.productinfoservice.core.model.ProgressStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreHashValidatorService {

    private final ProductInfoServiceCoreProgressTrackerService progressTrackerService;

    private final WebClient redirectedWebClient;

    public Mono<Pair<BuildInfo, Path>> validateSha256(BuildInfo buildInfo, Path path) {
        return redirectedWebClient.get()
            .uri(URI.create(buildInfo.checksumLink()))
            .accept(MediaType.TEXT_HTML)
            .retrieve()
            .bodyToMono(String.class)
            .flatMap(sha256expected -> Mono.fromRunnable(() -> validateSha256(buildInfo, path, sha256expected))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(err -> progressTrackerService.updateProgress(buildInfo, ProgressStatus.INVALID_CHECKSUM)))
            .thenReturn(Pair.of(buildInfo, path));
    }

    @SneakyThrows
    private void validateSha256(BuildInfo buildInfo, Path path, String sha256Expected) {
        try (var is = Files.newInputStream(path)) {
            var sha256Actual = DigestUtils.sha256Hex(is);

            if (!(StringUtils.hasLength(sha256Expected)
                && StringUtils.hasLength(sha256Actual)
                && sha256Expected.startsWith(sha256Actual + " "))) {
                progressTrackerService.updateProgress(buildInfo, ProgressStatus.INVALID_CHECKSUM);

                throw new IllegalArgumentException(String.format("Expected sha256=[%s] but actual=[%s]", sha256Expected, sha256Actual));
            }
        }
    }
}
