package com.lllbllllb.productinfoservice.repositoryremote.model.releasescode;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Releases(
    Map<String, List<Container>> productCodeToContainerMap
) {

}
