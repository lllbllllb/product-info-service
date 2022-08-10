package com.lllbllllb.productinfoservice.core;

import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreFileService {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final ProductInfoServiceCoreFileNameProvider fileNameProvider;

    public Mono<Pair<BuildInfo, Path>> writeToFile(BuildInfo buildInfo, Publisher<DataBuffer> publisher) {
        var filename = fileNameProvider.get(buildInfo);
        var path = Path.of(properties.getPathToSave(), filename);

        return DataBufferUtils.write(publisher, path, StandardOpenOption.CREATE)
            .thenReturn(Pair.of(buildInfo, path));
    }
}
