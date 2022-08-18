package com.lllbllllb.productinfoservice.model;

import java.time.LocalDate;

/**
 * Build metadata holder.
 */
public record BuildMetadata(
    String productName,
    String channelName,
    String channelStatus,
    String version,
    LocalDate releaseDate,
    String fullNumber,

    String productCode
) {

}
