package com.lllbllllb.productinfoservice.model;

import java.time.LocalDate;

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
