package com.lllbllllb.productinfoservice.core;

import java.nio.file.Path;
import java.util.Map;

import com.lllbllllb.productinfoservice.core.model.FileExtractorLabel;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * TarGzDecompressor.
 *
 * @author Yahor Pashkouski
 * @since 10.08.2022
 */
@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFileExtractionService {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final Map<FileExtractorLabel, ProductInfoServiceCoreFileExtractor> labelToFileExtractorMap;

    private final ProductInfoServiceCoreFailureService failureService;

    public Mono<BuildInfoAware<byte[]>> extractFileFromPath(BuildInfo buildInfo, Path path, Round round) {
        return extractFileFromPath(path)
            .onErrorResume(ex -> failureService.onErrorResume(ex, buildInfo, Status.INVALID_DATA, round, Mono.empty()))
            .map(bytes -> new BuildInfoAware<>(buildInfo, bytes));
    }

    private Mono<byte[]> extractFileFromPath(Path path) {
        var fileExtractorLabel = properties.getFileExtractorLabel();

        if (labelToFileExtractorMap.containsKey(fileExtractorLabel)) {
            return labelToFileExtractorMap.get(fileExtractorLabel).extractFileFromPath(path);
        }

        return Mono.error(() -> new UnsupportedOperationException("No extractor found for [%s]".formatted(fileExtractorLabel)));
    }

}
