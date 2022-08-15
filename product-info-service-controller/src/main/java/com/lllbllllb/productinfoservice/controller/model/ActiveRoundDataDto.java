package com.lllbllllb.productinfoservice.controller.model;

import java.time.LocalDate;

import com.lllbllllb.productinfoservice.model.Status;

public record ActiveRoundDataDto(
    String productCode,
    String productName,
    String version,
    String fullNumber,
    Status status,
    String link,
    LocalDate releaseDate,
    String roundCratedDate,
    String roundInstanceId
) {

}
