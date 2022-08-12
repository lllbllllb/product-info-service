package com.lllbllllb.productinfoservice.core;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.lllbllllb.productinfoservice.core.model.BuildInfoAware;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFileService {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceCoreFileNameProvider fileNameProvider;

    public Mono<BuildInfoAware<Path>> writeToFile(BuildInfoAware<Flux<DataBuffer>> buildInfoAware) {
        var buildInfo = buildInfoAware.buildInfo();
        var filename = fileNameProvider.get(buildInfo);
        var path = Path.of(properties.getPathToSave(), filename);

        return DataBufferUtils.write(buildInfoAware.obj(), path, StandardOpenOption.CREATE)
            .thenReturn(new BuildInfoAware<>(buildInfo, path));
    }
}
