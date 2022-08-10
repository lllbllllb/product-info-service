package com.lllbllllb.productinfoservice.core;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceCoreFileNameProvider {

    public String get(BuildInfo buildInfo) {
        var metadata = buildInfo.buildMetadata();

        return String.format("%s-%s-%s.tar.gz", metadata.productName(), metadata.releaseDate(), metadata.fullNumber());
    }
}
