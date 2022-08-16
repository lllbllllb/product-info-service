package com.lllbllllb.productinfoservice.functionalteast;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.UUID;

import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repositorylocal.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.repositorylocal.model.ProductInfoDto;
import com.lllbllllb.productinfoservice.repositorylocal.model.RoundDto;
import io.r2dbc.postgresql.codec.Json;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TestUtil {

    public static RoundDto getRound(String instanceId) {
        var roundDto = new RoundDto();
        roundDto.setId(UUID.nameUUIDFromBytes(instanceId.getBytes()));
        roundDto.setInstanceId(instanceId);
        roundDto.setCreatedDate(Instant.ofEpochMilli(instanceId.hashCode()));

        return roundDto;
    }

    public static BuildInfoDto getBuildInfo(String product, UUID roundId, Status status) {
        var buildInfoDto = new BuildInfoDto();
        buildInfoDto.setId(UUID.nameUUIDFromBytes(product.getBytes()));
        buildInfoDto.setProductCode(product.substring(0, 2).toUpperCase());
        buildInfoDto.setChecksum(DigestUtils.sha256Hex(product));
        buildInfoDto.setLink("https://dot.com/%s.tar.gz".formatted(product));
        buildInfoDto.setChecksumLink("https://dot.com/%s.tar.gz.sha256".formatted(product));
        buildInfoDto.setSize(product.hashCode());
        buildInfoDto.setProductName(product);
        buildInfoDto.setChannelName("%s RELEASE".formatted(product));
        buildInfoDto.setChannelStatus("release");
        buildInfoDto.setBuildVersion("buildVersion_%s".formatted(product));
        buildInfoDto.setReleaseDate(LocalDate.ofInstant(Instant.ofEpochMilli(product.hashCode() * 2L), ZoneId.systemDefault()));
        buildInfoDto.setFullNumber("fullNumber_%s".formatted(product));
        buildInfoDto.setStatus(status);
        buildInfoDto.setRoundId(roundId);

        return buildInfoDto;
    }

    public static ProductInfoDto getProductInfoDto(UUID bidId) {
        var json = """
            {
                "name": "someKey",
                "value": "%s"
            }
            """.formatted(bidId);
        var productInfoDto = new ProductInfoDto();
        productInfoDto.setBuildInfoId(bidId);
        productInfoDto.setProductInfo(Json.of(json));

        return productInfoDto;
    }

}
