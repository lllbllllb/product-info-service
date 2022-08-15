package com.lllbllllb.productinfoservice.controller.model;

import java.time.LocalDate;

public record BuildInfoDto(
    String link,
    String checksum,
    String productName,
    String version,
    LocalDate releaseDate,
    String fullNumber,
    String productCode,
    String roundInstanceId,
    String roundCratedDate
) {

}
