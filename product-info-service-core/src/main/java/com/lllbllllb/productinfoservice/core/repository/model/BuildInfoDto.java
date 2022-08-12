package com.lllbllllb.productinfoservice.core.repository.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import io.r2dbc.postgresql.codec.Json;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("build_info")
public class BuildInfoDto implements Persistable<UUID> {

    @Id
    private UUID id;

    @Indexed
    private String productCode;

    private Json productInfo;

    private String checksum;

    private String link;

    private String checksumLink;

    private long size;

    private String productName;

    private String channelName;

    private String channelStatus;

    private String buildVersion;

    private LocalDate releaseDate;

    private String fullNumber;

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDate lastModifiedDate;

    @Version
    private Long version;

    @Override
    public boolean isNew() {
        return version == null;
    }
}
