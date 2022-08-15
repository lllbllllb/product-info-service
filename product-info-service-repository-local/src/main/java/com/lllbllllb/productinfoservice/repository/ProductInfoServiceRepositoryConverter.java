package com.lllbllllb.productinfoservice.repository;

import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repository.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.repository.model.ProductInfoDto;
import com.lllbllllb.productinfoservice.repository.model.RoundDto;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryConverter {

    private final ProductInfoServiceRepositoryIdProvider idProvider;

    private final ObjectMapper objectMapper;

    public BuildInfoDto toDto(BuildInfo buildInfo, Status status, UUID roundId) {
        var metadata = buildInfo.buildMetadata();
        var dto = new BuildInfoDto();
        var id = idProvider.getBuildInfoId(buildInfo);
        dto.setId(id);
        dto.setProductCode(metadata.productCode());
        dto.setChecksum(buildInfo.checksum());
        dto.setChecksumLink(buildInfo.checksumLink());
        dto.setLink(buildInfo.link());
        dto.setSize(buildInfo.size());
        dto.setChannelName(buildInfo.buildMetadata().channelName());
        dto.setChannelStatus(buildInfo.buildMetadata().channelStatus());
        dto.setProductName(metadata.productName());
        dto.setBuildVersion(metadata.version());
        dto.setReleaseDate(metadata.releaseDate());
        dto.setFullNumber(metadata.fullNumber());
        dto.setStatus(status);
        dto.setRoundId(roundId);

        return dto;
    }

    public ProductInfoDto toDto(BuildInfo buildInfo, byte[] productInfo) {
        var dto = new ProductInfoDto();
        var id = idProvider.getBuildInfoId(buildInfo);
        dto.setBuildInfoId(id);
        dto.setProductInfo(toBuildInfoJson(productInfo));

        return dto;
    }

    public BuildInfoAware<Status> fromDto(BuildInfoDto dto) {
        var status = dto.getStatus();

        return fromDto(dto, status);
    }

    public <T> BuildInfoAware<T> fromDto(BuildInfoDto dto, T obj) {
        var buildMetadata = new BuildMetadata(
            dto.getProductName(),
            dto.getChannelName(),
            dto.getChannelStatus(),
            dto.getBuildVersion(),
            dto.getReleaseDate(),
            dto.getFullNumber(),
            dto.getProductCode()
        );
        var buildInfo = new BuildInfo(
            dto.getLink(),
            dto.getSize(),
            dto.getChecksumLink(),
            buildMetadata,
            dto.getChecksum()
        );

        return new BuildInfoAware<>(buildInfo, obj);
    }

    @SneakyThrows
    public ProductInfo fromDto(ProductInfoDto dto) {
        return new ProductInfo(
            objectMapper.readTree(dto.getProductInfo().asArray()),
            dto.getCreatedDate(),
            dto.getLastModifiedDate()
        );
    }

    public Json toBuildInfoJson(byte[] bytes) {
        return Json.of(bytes);
    }

    public RoundDto toDto(Round round) {
        var id = idProvider.getRoundId(round);
        var dto = new RoundDto();
        dto.setId(id);
        dto.setInstanceId(round.instanceId());
        dto.setCreatedDate(round.cratedDate());

        return dto;
    }

    public Round fromDto(RoundDto dto) {
        return new Round(
            dto.getInstanceId(),
            dto.getCreatedDate()
        );
    }
}
