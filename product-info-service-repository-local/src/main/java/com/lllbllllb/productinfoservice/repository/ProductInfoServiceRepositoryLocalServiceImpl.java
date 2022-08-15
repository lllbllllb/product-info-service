package com.lllbllllb.productinfoservice.repository;

import java.time.Instant;
import java.util.List;

import com.lllbllllb.productinfoservice.ProductInfoServiceRepositoryLocalService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
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

    private final ProductInfoServiceRoundRepository roundRepository;

    private final ProductInfoServiceRepositoryIdProvider idProvider;

    private final ProductInfoServiceRepositoryConverter converter;

    private final TransactionalOperator rxtx;

    @Override
    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round, Status status) {
        var buildInfoId = idProvider.getBuildInfoId(buildInfo);
        var roundId = idProvider.getRoundId(round);

        return buildInfoRepository.findById(buildInfoId)
            .map(dto -> {
                dto.setStatus(status);
                return dto;
            })
            .defaultIfEmpty(converter.toDto(buildInfo, status, roundId))
            .flatMap(buildInfoRepository::save)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

    @Override
    public Mono<BuildInfoAware<Status>> updateBuildInfo(BuildInfo buildInfo, Status status) {
        var id = idProvider.getBuildInfoId(buildInfo);

        return buildInfoRepository.findById(id)
            .map(dto -> {
                dto.setStatus(status);
                return dto;
            })
            .flatMap(buildInfoRepository::save)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }

    @Override
    public Mono<BuildInfoAware<ProductInfo>> saveProductInfo(BuildInfo buildInfo, byte[] bytes) {
        var id = idProvider.getBuildInfoId(buildInfo);

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
            .map(idProvider::getBuildInfoId)
            .toList();

        return buildInfoRepository.findAllById(ids)
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
    public Mono<Round> saveRound(Round round) {
        var dto = converter.toDto(round);

        return roundRepository.save(dto)
            .map(converter::fromDto)
            .as(rxtx::transactional);
    }


    @Override
    public Flux<BuildInfoAware<Round>> findAllFinishedBuildsByPeriod(Instant from, Instant to) {
        return buildInfoRepository.findAllFinishedByPeriod(from, to)
            .flatMap(buildInfoDto -> roundRepository.findById(buildInfoDto.getRoundId())
                .map(dto -> {
                    var round = converter.fromDto(dto);
                    return converter.fromDto(buildInfoDto, round);
                }))
            .as(rxtx::transactional);
    }

}
