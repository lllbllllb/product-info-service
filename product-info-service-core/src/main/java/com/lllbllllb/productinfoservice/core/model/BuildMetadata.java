package com.lllbllllb.productinfoservice.core.model;

import java.time.LocalDate;

public record BuildMetadata(
    String productName,
    String channelName,
    String channelStatus,
    String version,
    LocalDate releaseDate,
    String fullNumber
) {

}
