package com.lllbllllb.productinfoservice.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProductInfo(

    Object productInfoFile,

    LocalDateTime createdDate,

    LocalDate updatedDate
) {

}
