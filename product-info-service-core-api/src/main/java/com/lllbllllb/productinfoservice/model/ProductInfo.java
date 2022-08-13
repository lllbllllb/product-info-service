package com.lllbllllb.productinfoservice.model;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;

public record ProductInfo(

    JsonNode productInfoFile,

    Instant createdDate,

    Instant updatedDate
) {

}
