package com.lllbllllb.productinfoservice.repositorylocal;

import java.time.Instant;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.lllbllllb.productinfoservice.ProductInfoServiceBuildInfoRepositoryService;
import com.lllbllllb.productinfoservice.ProductInfoServiceProductInfoRepositoryService;
import com.lllbllllb.productinfoservice.ProductInfoServiceRoundRepositoryService;
import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.BuildInfoAware;
import com.lllbllllb.productinfoservice.model.ProductInfo;
import com.lllbllllb.productinfoservice.model.Round;
import com.lllbllllb.productinfoservice.model.Status;
import com.lllbllllb.productinfoservice.repositorylocal.model.BuildInfoDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryLocalServiceImpl
    implements ProductInfoServiceBuildInfoRepositoryService, ProductInfoServiceProductInfoRepositoryService, ProductInfoServiceRoundRepositoryService {

    private final ProductInfoServiceBuildInfoRepository buildInfoRepository;

    private final ProductInfoServiceProductInfoRepository productInfoRepository;

    private final ProductInfoServiceRoundRepository roundRepository;

    private final ProductInfoServiceRepositoryIdProvider idProvider;

    private final ProductInfoServiceRepositoryConverter converter;

    @Transactional
    @Override
    public Mono<BuildInfoAware<Status>> saveBuildInfo(BuildInfo buildInfo, Round round, Status status) {
        var buildInfoId = idProvider.getBuildInfoId(buildInfo);
        var roundId = idProvider.getRoundId(round);

        return buildInfoRepository.findById(buildInfoId)
            .map(dto -> {
                dto.setStatus(status);
                dto.setRoundId(roundId);
                return dto;
            })
            .defaultIfEmpty(converter.toDto(buildInfo, status, roundId))
            .flatMap(buildInfoRepository::save)
            .log(this.getClass().getName())
            .map(converter::fromDto);
    }

    @Transactional
    @Override
    public Mono<BuildInfoAware<Status>> updateBuildInfo(BuildInfo buildInfo, Status status) {
        var id = idProvider.getBuildInfoId(buildInfo);

        return buildInfoRepository.findById(id)
            .map(dto -> {
                dto.setStatus(status);
                return dto;
            })
            .flatMap(buildInfoRepository::save)
            .log(this.getClass().getName())
            .map(converter::fromDto);
    }

    @Transactional
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
            .log(this.getClass().getName())
            .map(dto -> new BuildInfoAware<>(buildInfo, converter.fromDto(dto)));
    }

    @Transactional(readOnly = true)
    public Mono<BuildInfoAware<Status>> findBuildInfo(BuildInfo buildInfo) {
        var id = idProvider.getBuildInfoId(buildInfo);

        return buildInfoRepository.findById(id)
            .log(this.getClass().getName(), Level.FINE)
            .map(converter::fromDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<ProductInfo> findProductInfoByProductCode(String productCode) {
        return productInfoRepository.findAllByProductCode(productCode)
            .log(this.getClass().getName(), Level.FINE)
            .map(converter::fromDto);
    }

    @Transactional(readOnly = true)
    @Override
    public Mono<ProductInfo> findProductInfoByProductCodeAndFullNumber(String productCode, String fullNumber) {
        return productInfoRepository.findByProductCodeAndFullNumber(productCode, fullNumber)
            .log(this.getClass().getName(), Level.FINE)
            .map(converter::fromDto);
    }

    @Transactional
    @Override
    public Mono<Round> saveRound(Round round) {
        var dto = converter.toDto(round);

        return roundRepository.save(dto)
            .log(this.getClass().getName())
            .map(converter::fromDto);
    }


    @Transactional(readOnly = true)
    @Override
    public Flux<BuildInfoAware<Round>> findAllFinishedBuildsByPeriod(Instant from, Instant to) {
        return buildInfoRepository.findAllFinishedByPeriod(from, to)
            .log(this.getClass().getName(), Level.FINE)
            .flatMap(buildInfoDto -> roundRepository.findById(buildInfoDto.getRoundId())
                .map(dto -> {
                    var round = converter.fromDto(dto);
                    return converter.fromDto(buildInfoDto, round);
                }));
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<BuildInfoAware<Pair<Status, Round>>> findAllFromActiveRounds() {
        return buildInfoRepository.findAllByStatus(Status.IN_PROGRESS)
            .map(BuildInfoDto::getRoundId)
            .collect(Collectors.toSet())
            .flatMapMany(roundRepository::findAllById)
            .log(this.getClass().getName(), Level.FINE)
            .flatMap(roundDto -> buildInfoRepository.findAllByRoundId(roundDto.getId())
                .map(buildInfoDto -> {
                    var round = converter.fromDto(roundDto);

                    return converter.fromDto(buildInfoDto, Pair.of(buildInfoDto.getStatus(), round));
                }));
    }
}
