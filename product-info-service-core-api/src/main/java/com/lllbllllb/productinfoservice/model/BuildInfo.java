package com.lllbllllb.productinfoservice.model;

public record BuildInfo(
    String link,
    long size,
    String checksumLink,
    BuildMetadata buildMetadata,
    String checksum
) {

}
