package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceRepositoryBuildInfoIdProvider {

    public UUID get(BuildInfo buildInfo) {
        var metadata = buildInfo.buildMetadata();
        var link = ("%s_%s".formatted(metadata.productCode(), metadata.fullNumber())).getBytes();

        return UUID.nameUUIDFromBytes(link);
    }
}
