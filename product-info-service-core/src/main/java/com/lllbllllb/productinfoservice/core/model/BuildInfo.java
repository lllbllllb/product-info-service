package com.lllbllllb.productinfoservice.core.model;

public record BuildInfo(
    String link,
    long size,
    String checksumLink,
    BuildMetadata buildMetadata,
    String productCode,
    String checksum
) {

}
