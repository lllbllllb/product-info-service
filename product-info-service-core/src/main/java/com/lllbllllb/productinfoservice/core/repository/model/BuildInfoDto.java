package com.lllbllllb.productinfoservice.core.repository.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("build-info")
public class BuildInfoDto {

    @Id
    private UUID id;

    @Indexed
    private String productCode;

    private Object productInfo;

    private String checksum;

    private String link;

    private String checksumLink;

    private long size;

    private String productName;

    private String channelName;

    private String channelStatus;

    private String version;

    private LocalDate releaseDate;

    @Indexed
    private String fullNumber;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDate lastModifiedDate;
}
