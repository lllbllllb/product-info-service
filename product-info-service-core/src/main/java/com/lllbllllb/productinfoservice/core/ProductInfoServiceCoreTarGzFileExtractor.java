package com.lllbllllb.productinfoservice.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.lllbllllb.productinfoservice.core.model.FileExtractorLabel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCoreTarGzFileExtractor implements ProductInfoServiceCoreFileExtractor {

    private final ProductInfoServiceCoreConfigurationProperties properties;

    @Override
    public FileExtractorLabel getLabel() {
        return FileExtractorLabel.TAR_GZ;
    }

    @Override
    public Mono<byte[]> extractFileFromPath(Path path) {
        var fileName = properties.getTargetFileName();

        return Mono.fromCallable(() -> {
                if (Files.notExists(path)) {
                    throw new UncheckedIOException(new IOException("File doesn't exists!"));
                }

                try (
                    var fi = Files.newInputStream(path);
                    var bi = new BufferedInputStream(fi);
                    var gzi = new GzipCompressorInputStream(bi);
                    var ti = new TarArchiveInputStream(gzi)
                ) {
                    ArchiveEntry entry;
                    while ((entry = ti.getNextEntry()) != null) {
                        if (entry.getName().contains(fileName)) {
                            var buf = new byte[(int) entry.getSize()];

                            IOUtils.readFully(ti, buf);

                            return buf;
                        }
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }

                throw new IllegalStateException(String.format("No file with name [%s] found inside [%s]", fileName, path));
            })
            .subscribeOn(Schedulers.boundedElastic());
    }
}
