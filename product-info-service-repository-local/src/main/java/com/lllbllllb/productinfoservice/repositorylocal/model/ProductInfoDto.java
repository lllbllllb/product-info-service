package com.lllbllllb.productinfoservice.repositorylocal.model;

import java.time.Instant;
import java.util.UUID;

import io.r2dbc.postgresql.codec.Json;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("product_info")
public class ProductInfoDto implements Persistable<UUID> {

    @Id
    private UUID buildInfoId;

    private Json productInfo;

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Version
    private Long version;

    @Override
    public UUID getId() {
        return buildInfoId;
    }

    @Override
    public boolean isNew() {
        return version == null;
    }
}
