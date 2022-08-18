package com.lllbllllb.productinfoservice.repositorylocal;

import java.util.UUID;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Round;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Id provider for local dtos.
 */
@Service
@RequiredArgsConstructor
public class ProductInfoServiceRepositoryIdProvider {

    /**
     * Get id for {@link BuildInfo}.
     *
     * @param buildInfo {@link BuildInfo}
     * @return generated {@link UUID}
     */
    public UUID getBuildInfoId(BuildInfo buildInfo) {
        var metadata = buildInfo.buildMetadata();
        var link = ("%s_%s".formatted(metadata.productCode(), metadata.fullNumber())).getBytes();

        return UUID.nameUUIDFromBytes(link);
    }

    /**
     * Get id for {@link Round}.
     *
     * @param round {@link Round}
     * @return generated {@link UUID}
     */
    public UUID getRoundId(Round round) {
        var link = ("%s_%s".formatted(round.instanceId(), round.cratedDate())).getBytes();

        return UUID.nameUUIDFromBytes(link);
    }
}
