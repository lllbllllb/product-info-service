package com.lllbllllb.productinfoservice.repository;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryServiceImpl implements ProductInfoServiceRepositoryService {

    private final ProductInfoServiceRepositoryPostgres repository;

    private final ProductInfoServiceRepositoryIdProvider idProvider;

    private final ProductInfoServiceRepositoryConverter converter;

    @Override
    public Mono<BuildInfoAware<ProductInfo>> save(BuildInfoAware<byte[]> buildInfoAware) {
        return repository.save(converter.toDto(buildInfoAware))
            .map(converter::fromDto);
    }

    @Override
    public Flux<BuildInfoAware<ProductInfo>> findAllByBuildInfo(List<BuildInfo> buildInfos) {
        var ids = buildInfos.stream()
            .map(idProvider::get)
            .toList();

        return repository.findAllById(ids)
            .map(converter::fromDto);
    }

    @Override
    public Flux<BuildInfoAware<ProductInfo>> findByProductCode(String productCode) {
        return repository.findAllByProductCode(productCode)
            .map(converter::fromDto);
    }

    @Override
    public Mono<BuildInfoAware<ProductInfo>> findByProductCodeAndFullNumber(String productCode, String fullNumber) {
        return repository.findByProductCodeAndFullNumber(productCode, fullNumber)
            .map(converter::fromDto);
    }
}
