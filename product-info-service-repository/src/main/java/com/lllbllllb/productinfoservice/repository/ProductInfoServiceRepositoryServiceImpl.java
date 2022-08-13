package com.lllbllllb.productinfoservice.repository;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryServiceImpl implements ProductInfoServiceRepositoryService {

    private final ProductInfoServiceBuildInfoRepository buildInfoRepository;

    private final ProductInfoServiceProductInfoRepository productInfoRepository;

    private final ProductInfoServiceRepositoryIdProvider idProvider;

    private final ProductInfoServiceRepositoryConverter converter;

    @Override
    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Status status) {
        var dto = converter.toDto(buildInfo, status);

        return buildInfoRepository.save(dto)
            .map(converter::fromDto);
    }

    @Override
    public Mono<ProductInfo> saveProductInfo(BuildInfoAware<byte[]> buildInfoAware) {
        return productInfoRepository.save(converter.toDto(buildInfoAware))
            .map(converter::fromDto);
    }

    @Override
    public Flux<BuildInfoAware<Status>> findAllBuildInfo(List<BuildInfo> buildInfos) {
        var ids = buildInfos.stream()
            .map(idProvider::get)
            .toList();

        return buildInfoRepository.findAllById(ids)
            .map(converter::fromDto);
    }

    @Override
    public Flux<ProductInfo> findAllProductInfo(List<BuildInfo> buildInfos) {
        var ids = buildInfos.stream()
            .map(idProvider::get)
            .toList();

        return productInfoRepository.findAllById(ids)
            .map(converter::fromDto);
    }

    @Override
    public Flux<ProductInfo> findProductInfoByProductCode(String productCode) {
        return productInfoRepository.findAllByProductCode(productCode)
            .map(converter::fromDto);
    }

    @Override
    public Mono<ProductInfo> findProductInfoByProductCodeAndFullNumber(String productCode, String fullNumber) {
        return productInfoRepository.findByProductCodeAndFullNumber(productCode, fullNumber)
            .map(converter::fromDto);
    }
}
