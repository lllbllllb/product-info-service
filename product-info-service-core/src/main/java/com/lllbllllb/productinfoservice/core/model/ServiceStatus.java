package com.lllbllllb.productinfoservice.core.model;

import java.util.Map;

import lombok.Value;

@Value
public class ServiceStatus {

    Map<BuildInfo, ProgressStatus> progressMap;
}
