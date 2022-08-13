package com.lllbllllb.productinfoservice.core;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProgressStatus;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreChecksumService {

    private final ProductInfoServiceCoreProgressTrackerService progressTrackerService;

    private final WebClient redirectedWebClient;

    private final ProductInfoServiceRepositoryService repositoryService;

    private final ProductInfoServiceCoreFileService fileService;

    public Mono<String> getExpectedChecksum(String checksumLink) {
        return redirectedWebClient.get()
            .uri(URI.create(checksumLink))
            .accept(MediaType.TEXT_HTML)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<String> getActualChecksum(Path path) {
        return Mono.fromCallable(() -> {
                try (var is = Files.newInputStream(path)) {
                    return DigestUtils.sha256Hex(is);
                }
            })
            .subscribeOn(Schedulers.boundedElastic())
            .onErrorResume(e -> Mono.empty());
    }

    public Mono<BuildInfoAware<Path>> validateFileChecksum(BuildInfoAware<Path> buildInfoAware) {
        return getActualChecksum(buildInfoAware.obj())
            .handle((actualChecksum, sink) -> {
                var expectedChecksum = buildInfoAware.buildInfo().checksum();

                if (isChecksumTheSame(expectedChecksum, actualChecksum)) {
                    sink.next(buildInfoAware);
                } else {
                    progressTrackerService.updateProgress(buildInfoAware.buildInfo(), ProgressStatus.INVALID_CHECKSUM);
                    fileService.deleteFile(buildInfoAware.buildInfo()).subscribe();
                }
            });
    }

    public Flux<BuildInfo> filterUnchangedByChecksums(List<BuildInfo> buildInfos) {
        return repositoryService.findAllByBuildInfo(buildInfos)
            .collect(Collectors.toSet())
            .flatMapMany(buildInfoAwareSet -> {
                var fullNumberToChecksumMap = buildInfoAwareSet.stream()
                    .collect(Collectors.toMap(bia -> bia.buildInfo().buildMetadata().fullNumber(), bia -> bia.buildInfo().checksum()));

                var buildInfosToUpdateSteam = buildInfos.stream()
                    .filter(buildInfo -> {
                        var fullNumber = buildInfo.buildMetadata().fullNumber();
                        return !fullNumberToChecksumMap.containsKey(fullNumber)
                            || !isChecksumTheSame(fullNumberToChecksumMap.get(fullNumber), buildInfo.checksum());
                    });

               return Flux.fromStream(buildInfosToUpdateSteam);
            });
    }

    public boolean isChecksumTheSame(String sha256Expected, String sha256Actual) {
        return StringUtils.hasLength(sha256Expected)
            && StringUtils.hasLength(sha256Actual)
            && sha256Expected.equals(sha256Actual)
            || sha256Expected.startsWith(sha256Actual + " ");
    }
}
