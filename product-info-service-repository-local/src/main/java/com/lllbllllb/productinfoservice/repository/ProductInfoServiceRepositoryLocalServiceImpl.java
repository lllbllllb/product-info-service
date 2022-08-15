package com.lllbllllb.productinfoservice.repository;

import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryLocalService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryLocalServiceImpl implements ProductInfoServiceRepositoryLocalService {

    private final ProductInfoServiceBuildInfoRepository buildInfoRepository;

    private final ProductInfoServiceProductInfoRepository productInfoRepository;

    private final ProductInfoServiceRepositoryBuildInfoIdProvider buildInfoIdProvider;

    private final ProductInfoServiceRepositoryConverter converter;

    private final TransactionalOperator rxtx;

    @Override
    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Status status) {
        var id = buildInfoIdProvider.get(buildInfo);

        return buildInfoRepository.findById(id)
            .map(dto -> {
                dto.setStatus(status);
                return dto;
            })
            .defaultIfEmpty(converter.toDto(buildInfo, status))
            .flatMap(buildInfoRepository::save)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

    @Override
    public Mono<BuildInfoAware<ProductInfo>> saveProductInfo(BuildInfo buildInfo, byte[] bytes) {
        var id = buildInfoIdProvider.get(buildInfo);

        return productInfoRepository.findById(id)
            .map(dto -> {
                dto.setProductInfo(converter.toBuildInfoJson(bytes));
                return dto;
            })
            .defaultIfEmpty(converter.toDto(buildInfo, bytes))
            .flatMap(productInfoRepository::save)
            .map(dto -> new BuildInfoAware<>(buildInfo, converter.fromDto(dto)))
            .as(rxtx::transactional);
    }

    @Override
    public Flux<BuildInfoAware<Status>> findAllBuildInfo(List<BuildInfo> buildInfos) {
        var ids = buildInfos.stream()
            .map(buildInfoIdProvider::get)
            .toList();

        return buildInfoRepository.findAllById(ids)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

    @Override
    public Flux<ProductInfo> findAllProductInfo(List<BuildInfo> buildInfos) {
        var ids = buildInfos.stream()
            .map(buildInfoIdProvider::get)
            .toList();

        return productInfoRepository.findAllById(ids)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

    @Override
    public Flux<ProductInfo> findProductInfoByProductCode(String productCode) {
        return productInfoRepository.findAllByProductCode(productCode)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

    @Override
    public Mono<ProductInfo> findProductInfoByProductCodeAndFullNumber(String productCode, String fullNumber) {
        return productInfoRepository.findByProductCodeAndFullNumber(productCode, fullNumber)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

    @Override
    public Flux<BuildInfoAware<Status>> updateBuildInfoToStatus(Status from, Status to) {

        return buildInfoRepository.findAllByStatus(from)
            .map(dto -> {
                dto.setStatus(to);

                return dto;
            })
            .flatMap(buildInfoRepository::save)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

}
