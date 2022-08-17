package com.lllbllllb.productinfoservice.core;

import java.nio.file.Path;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFileCacheService {

    private final ProductInfoServiceCoreFileService fileService;

    private final ProductInfoServiceCoreFailureService failureService;

    public Mono<BuildInfoAware<Path>> writeToFile(BuildInfo buildInfo, Publisher<DataBuffer> dataBufferPublisher, Round round) {
        return fileService.getPath(buildInfo, round)
            .flatMap(path -> DataBufferUtils.write(dataBufferPublisher, path)
                .onErrorResume(ex -> failureService.onErrorResume(ex, buildInfo, Status.FAILED_WRITE_TO_FILE, round, Mono.empty()))
                .thenReturn(new BuildInfoAware<>(buildInfo, path)));
    }

}
