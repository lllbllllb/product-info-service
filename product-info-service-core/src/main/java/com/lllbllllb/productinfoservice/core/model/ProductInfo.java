package com.lllbllllb.productinfoservice.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;

public record ProductInfo(

    JsonNode productInfoFile,

    LocalDateTime createdDate,

    LocalDate updatedDate
) {

}
