package com.lllbllllb.productinfoservice.model;

import java.util.Map;

import lombok.Value;

@Value
public class ServiceStatus {

    Map<BuildInfo, Status> progressMap;
}
