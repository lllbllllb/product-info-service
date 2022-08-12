package com.lllbllllb.productinfoservice.core.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceRepositoryIdProvider {

    public UUID get(BuildInfo buildInfo) {
        var link = ("%s_%s".formatted(buildInfo.productCode(), buildInfo.buildMetadata().fullNumber())).getBytes();

        return UUID.nameUUIDFromBytes(link);
    }
}
