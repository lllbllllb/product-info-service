package com.lllbllllb.productinfoservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.BuildMetadata;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repository.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.repository.model.ProductInfoDto;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryConverter {

    private final ProductInfoServiceRepositoryBuildInfoIdProvider idProvider;

    private final ObjectMapper objectMapper;

    public BuildInfoDto toDto(BuildInfo buildInfo, Status status) {
        var metadata = buildInfo.buildMetadata();
        var dto = new BuildInfoDto();
        var id = idProvider.get(buildInfo);
        dto.setId(id);
        dto.setProductCode(buildInfo.productCode());
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

        return dto;
    }

    public ProductInfoDto toDto(BuildInfo buildInfo, byte[] productInfo) {
        var dto = new ProductInfoDto();
        var id = idProvider.get(buildInfo);
        dto.setBuildInfoId(id);
        dto.setProductInfo(toBuildInfoJson(productInfo));

        return dto;
    }

    @SneakyThrows
    public BuildInfoAware<Status> fromDto(BuildInfoDto dto) {
        var buildMetadata = new BuildMetadata(
            dto.getProductName(),
            dto.getChannelName(),
            dto.getChannelStatus(),
            dto.getBuildVersion(),
            dto.getReleaseDate(),
            dto.getFullNumber()
        );
        var buildInfo = new BuildInfo(
            dto.getLink(),
            dto.getSize(),
            dto.getChecksumLink(),
            buildMetadata,
            dto.getProductCode(),
            dto.getChecksum()
        );
        var status = dto.getStatus();

        return new BuildInfoAware<>(buildInfo, status);
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
}
