package com.lllbllllb.productinfoservice.core;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.lllbllllb.productinfoservice.core.model.BuildInfo;
import com.lllbllllb.productinfoservice.core.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.core.model.BuildMetadata;
import com.lllbllllb.productinfoservice.core.model.ProductInfo;
import com.lllbllllb.productinfoservice.core.repository.ProductInfoServiceRepository;
import com.lllbllllb.productinfoservice.core.repository.ProductInfoServiceRepositoryIdProvider;
import com.lllbllllb.productinfoservice.core.repository.model.BuildInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceCorePersistenceService {

    private final ProductInfoServiceRepository repository;

    private final ProductInfoServiceRepositoryIdProvider idProvider;

    public Mono<BuildInfoAware<ProductInfo>> save(BuildInfoAware<byte[]> buildInfoAware) {
        var buildInfo = buildInfoAware.buildInfo();
        var productInfoJson = buildInfoAware.obj();

        var metadata = buildInfo.buildMetadata();
        var dto = new BuildInfoDto();
        dto.setId(idProvider.get(buildInfo));
        var productInfo = new String(productInfoJson, StandardCharsets.UTF_8);
        dto.setProductCode(buildInfo.productCode());
        dto.setProductInfo(productInfo);
        dto.setChecksum(buildInfo.checksum());
        dto.setLink(buildInfo.link());
        dto.setProductName(metadata.productName());
        dto.setVersion(metadata.version());
        dto.setReleaseDate(metadata.releaseDate());
        dto.setFullNumber(metadata.fullNumber());

        return repository.save(dto)
            .map(this::fromDto);
    }

    public Flux<BuildInfoAware<ProductInfo>> findAllByBuildInfo(List<BuildInfo> buildInfos) {
        var ids = buildInfos.stream()
            .map(idProvider::get)
            .toList();

        return repository.findAllById(ids)
            .map(this::fromDto);
    }

    public Flux<BuildInfoAware<ProductInfo>> findByProductCode(String productCode) {
        return repository.findAllByProductCode(productCode)
            .map(this::fromDto);
    }

    public Mono<BuildInfoAware<ProductInfo>> findByProductCodeAndFullNumber(String productCode, String fullNumber) {
        return repository.findByProductCodeAndFullNumber(productCode, fullNumber)
            .map(this::fromDto);
    }

    // fixme: move to dedicated ConverterService
    private BuildInfoAware<ProductInfo> fromDto(BuildInfoDto dto) {
        var buildMetadata = new BuildMetadata(
            dto.getProductName(),
            dto.getChannelName(),
            dto.getChannelStatus(),
            dto.getVersion(),
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
        var productInfo = new ProductInfo(
            dto.getProductInfo(),
            dto.getCreatedDate(),
            dto.getLastModifiedDate()
        );


        return new BuildInfoAware<>(buildInfo, productInfo);
    }
}
