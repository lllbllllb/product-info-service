package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceRepositoryIdProvider {

    public UUID get(BuildInfo buildInfo) {
        var link = ("%s_%s".formatted(buildInfo.productCode(), buildInfo.buildMetadata().fullNumber())).getBytes();

        return UUID.nameUUIDFromBytes(link);
    }
}
