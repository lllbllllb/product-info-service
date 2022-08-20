package com.lllbllllb.productinfoservice.core;

import java.nio.file.Path;

import com.lllbllllb.productinfoservice.core.model.FileExtractorLabel;
import reactor.core.publisher.Mono;

public interface ProductInfoServiceCoreFileExtractor extends Label<FileExtractorLabel> {

    Mono<byte[]> extractFileFromPath(Path path);
}
