package com.lllbllllb.productinfoservice.model;

/**
 * Build info holder.
 */
public record BuildInfo(
    String link,
    long size,
    String checksumLink,
    BuildMetadata buildMetadata,
    String checksum
) {

}
