package com.lllbllllb.productinfoservice.core.model.releasescode;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Releases(
    List<Container> productCodeToContainerMap
) {

}
