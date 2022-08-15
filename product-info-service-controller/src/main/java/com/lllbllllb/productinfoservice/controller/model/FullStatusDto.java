package com.lllbllllb.productinfoservice.controller.model;

import java.util.Collection;

public record FullStatusDto(
    Collection<ActiveRoundDataDto> activeRoundsData,
    Collection<BuildInfoDto> lastBuildsInfo
) {

}
