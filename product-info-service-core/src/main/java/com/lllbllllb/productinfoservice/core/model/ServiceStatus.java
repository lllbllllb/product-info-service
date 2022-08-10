package com.lllbllllb.productinfoservice.core.model;

import java.util.Map;

import com.lllbllllb.productinfoservice.core.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceStatus {

    private Map<BuildInfo, ProgressStatus> progressMap;
}
