package com.lllbllllb.productinfoservice.core.repository;

import java.util.UUID;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceRepositoryIdProvider {

    public UUID get(BuildInfo buildInfo) {
        var link = buildInfo.link().getBytes();

        return UUID.nameUUIDFromBytes(link);
    }
}
