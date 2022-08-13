package com.lllbllllb.productinfoservice.core;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
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

    public Mono<BuildInfoAware<Path>> writeToFile(BuildInfo buildInfo, Publisher<DataBuffer> dataBufferPublisher) {
        var path = fileService.getPath(buildInfo);

        return DataBufferUtils.write(dataBufferPublisher, path, StandardOpenOption.CREATE)
            .onErrorResume(ex -> failureService.onErrorResume(buildInfo, Status.FAILED_WRITE_TO_FILE, Mono.empty()))
            .thenReturn(new BuildInfoAware<>(buildInfo, path));
    }

}
