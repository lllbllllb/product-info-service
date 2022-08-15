package com.lllbllllb.productinfoservice.repositoryremote.model.releasescode;

import java.util.Map;

public record Downloads(
    Map<String, Download> distroToDownloadMap
) {

}
