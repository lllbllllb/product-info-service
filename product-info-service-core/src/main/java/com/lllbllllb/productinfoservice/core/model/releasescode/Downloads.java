package com.lllbllllb.productinfoservice.core.model.releasescode;

import java.util.Map;

import com.lllbllllb.productinfoservice.model.BuildInfo;

public record Downloads(
    Map<String, BuildInfo> distroToDownloadMap
) {

}
