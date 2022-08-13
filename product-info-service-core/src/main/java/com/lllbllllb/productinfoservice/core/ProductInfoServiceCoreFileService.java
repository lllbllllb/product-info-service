package com.lllbllllb.productinfoservice.core;

import java.nio.file.Files;
import java.nio.file.Path;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFileService {

    public static final long NO_FILE_SIZE = 0;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    public String getName(BuildInfo buildInfo) {
        var metadata = buildInfo.buildMetadata();

        return String.format("%s-%s-%s.tar.gz", metadata.productName(), metadata.releaseDate(), metadata.fullNumber());
    }

    public Path getPath(BuildInfo buildInfo) {
        var fileName = getName(buildInfo);

        return Path.of(properties.getPathToSaveTmp(), fileName);
    }

    public Mono<Long> getFileSize(BuildInfo buildInfo) {
        var path = getPath(buildInfo);

        return getFileSize(path);
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

    public Mono<Boolean> deleteFile(BuildInfo buildInfo) {
        var path = getPath(buildInfo);

        return deleteFile(path);
    }

    public Mono<Boolean> deleteFile(Path path) {
        return Mono.fromCallable(() -> Files.deleteIfExists(path))
            .subscribeOn(Schedulers.boundedElastic());
    }
}
