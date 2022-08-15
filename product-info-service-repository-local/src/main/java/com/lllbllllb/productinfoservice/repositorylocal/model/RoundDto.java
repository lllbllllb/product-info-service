package com.lllbllllb.productinfoservice.repositorylocal.model;

import java.time.Instant;
import java.util.UUID;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("round")
public class RoundDto implements Persistable<UUID> {

    @Id
    private UUID id;

    private String instanceId;

    private Instant createdDate;

    @LastModifiedDate
    private Instant lastModifiedDate;

    @Version
    private Long version;

    @Override
    public boolean isNew() {
        return version == null;
    }
}
