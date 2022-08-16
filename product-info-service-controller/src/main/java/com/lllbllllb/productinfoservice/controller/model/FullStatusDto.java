package com.lllbllllb.productinfoservice.controller.model;

import java.util.List;

public record FullStatusDto(
    List<ActiveRoundDataDto> activeRoundsData,
    List<BuildInfoDto> lastBuildsInfo
) {

}
