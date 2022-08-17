package com.lllbllllb.productinfoservice.core;

import java.nio.file.Files;
import java.nio.file.Path;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFileService {

    public static final long NO_FILE_SIZE = 0;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public String getName(BuildInfo buildInfo, Round round) {
        var metadata = buildInfo.buildMetadata();

        return "%s_%s_%s_%s_%s_%s.tar.gz".formatted(
            metadata.productName(),
            metadata.productCode(),
            metadata.releaseDate(),
            metadata.fullNumber(),
            buildInfo.checksum(),
            round.instanceId()
        );
    }

    public Mono<Path> getPath(BuildInfo buildInfo, Round round) {
        var fileName = getName(buildInfo, round);

        return Mono.fromCallable(() -> Files.createDirectories(Path.of(properties.getPathToSaveTmp())))
            .subscribeOn(Schedulers.boundedElastic())
            .map(path -> path.resolve(fileName));
    }

    public Mono<Long> getFileSize(BuildInfo buildInfo, Round round) {
        return getPath(buildInfo, round)
            .flatMap(this::getFileSize);
    }

    public Mono<Long> getFileSize(Path path) {
        if (isFileExists(path)) {
            return Mono.fromCallable(() -> Files.size(path))
                .subscribeOn(Schedulers.boundedElastic());
        }

        return Mono.just(NO_FILE_SIZE);
    }

    public boolean isFileExists(Path path) {
        return Files.exists(path);
    }

    public Mono<Boolean> deleteFile(BuildInfo buildInfo, Round round) {
        return getPath(buildInfo, round)
            .flatMap(this::deleteFile);
    }

    public Mono<Boolean> deleteFile(Path path) {
        return Mono.fromCallable(() -> Files.deleteIfExists(path))
            .subscribeOn(Schedulers.boundedElastic());
    }
}
