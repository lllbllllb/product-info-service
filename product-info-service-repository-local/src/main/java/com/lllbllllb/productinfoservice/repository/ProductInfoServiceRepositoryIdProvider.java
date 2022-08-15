package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryIdProvider {

    public UUID getBuildInfoId(BuildInfo buildInfo) {
        var metadata = buildInfo.buildMetadata();
        var link = ("%s_%s".formatted(metadata.productCode(), metadata.fullNumber())).getBytes();

        return UUID.nameUUIDFromBytes(link);
    }

    public UUID getRoundId(Round round) {
        var link = ("%s_%s".formatted(round.instanceId(), round.cratedDate())).getBytes();

        return UUID.nameUUIDFromBytes(link);
    }
}
