package com.lllbllllb.productinfoservice.controller;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.lllbllllb.productinfoservice.controller.model.ActiveRoundDataDto;
import com.lllbllllb.productinfoservice.controller.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceControllerConverterService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BuildInfoDto toBuildInfoDto(BuildInfoAware<Round> buildInfoAware) {
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

    public ActiveRoundDataDto toActiveRoundDataDto(BuildInfoAware<Pair<Status, Round>> buildInfoAware) {
        var buildInfo = buildInfoAware.buildInfo();
        var metadata = buildInfo.buildMetadata();
        var status = buildInfoAware.obj().getLeft();
        var round = buildInfoAware.obj().getRight();
        var createdStr = FORMATTER.format(ZonedDateTime.ofInstant(round.cratedDate(), ZoneOffset.UTC));

        return new ActiveRoundDataDto(
            metadata.productCode(),
            metadata.productName(),
            metadata.version(),
            metadata.fullNumber(),
            status,
            buildInfo.link(),
            metadata.releaseDate(),
            createdStr,
            round.instanceId()
        );
    }
}
