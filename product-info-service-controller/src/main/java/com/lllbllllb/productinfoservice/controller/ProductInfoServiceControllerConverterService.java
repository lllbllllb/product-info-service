package com.lllbllllb.productinfoservice.controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.lllbllllb.productinfoservice.controller.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceControllerConverterService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Clock clock;

    public BuildInfoDto toDto(BuildInfoAware<Round> buildInfoAware) {
        var buildInfo = buildInfoAware.buildInfo();
        var metadata = buildInfo.buildMetadata();
        var round = buildInfoAware.obj();
        var createdStr = FORMATTER.format(ZonedDateTime.ofInstant(round.cratedDate(), ZoneOffset.UTC));

        return new BuildInfoDto(
            buildInfo.link(),
            buildInfo.checksum(),
            metadata.productName(),
            metadata.version(),
            metadata.releaseDate(),
            metadata.fullNumber(),
            metadata.productCode(),
            round.instanceId(),
            createdStr
        );
    }
}
