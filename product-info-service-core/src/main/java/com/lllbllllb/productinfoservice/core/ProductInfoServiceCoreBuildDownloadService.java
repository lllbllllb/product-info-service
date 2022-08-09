package com.lllbllllb.productinfoservice.core;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import io.netty.channel.ChannelException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreBuildDownloadService {

    private final WebClient webClient;

    private final ProductInfoServiceCoreConfigurationProperties properties;

    private final Map<BuildInfo, ProgressStatus> progressMap = new ConcurrentHashMap<>();

    @SneakyThrows
    public void downloadBuild(BuildInfo buildInfo) {
        progressMap.put(buildInfo, ProgressStatus.RUNNING);

        var dataStream = webClient.get()
            .uri(buildInfo.link())
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .header("Range", String.format("bytes=%d-", buildInfo.size()))
            .retrieve()
            .bodyToFlux(DataBuffer.class)
            .doOnError(err -> {
                progressMap.put(buildInfo, ProgressStatus.FAILED);
                System.out.println(progressMap);
            })
            .doOnNext(ddb -> System.out.println(progressMap));

        var path = Path.of(properties.getPathToSave(), getFileName(buildInfo));
        var fileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        DataBufferUtils.write(dataStream, fileChannel)
            .publishOn(Schedulers.boundedElastic())
            .map(DataBufferUtils::release)
            .doOnError(throwable -> {
                try {
                    fileChannel.force(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
//            .retryWhen(Retry.any().fixedBackoff(Duration.ofSeconds(5)).retryMax(5))
            .doOnComplete(() -> {
                try {
                    fileChannel.force(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
            .doOnError(e -> !(e instanceof ChannelException), e -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            })
            .doOnError(ChannelException.class, e -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            })
            .doOnTerminate(() -> {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            })
            .share()
            .blockLast();
    }

    private String getFileName(BuildInfo buildInfo) {
        var metadata = buildInfo.buildMetadata();
        return String.format("%s-%s-%s.tar.gz", metadata.productName(), metadata.releaseDate(), metadata.fullNumber());
    }
}
