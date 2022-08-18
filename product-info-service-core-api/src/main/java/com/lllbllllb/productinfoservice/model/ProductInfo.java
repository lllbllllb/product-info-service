package com.lllbllllb.productinfoservice.model;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Data about {@code product-info.json} holder.
 */
public record ProductInfo(

    JsonNode productInfoFile,

    Instant createdDate,

    Instant updatedDate
) {

}
