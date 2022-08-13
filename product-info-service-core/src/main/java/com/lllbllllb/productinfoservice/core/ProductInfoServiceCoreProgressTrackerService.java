package com.lllbllllb.productinfoservice.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.ProgressStatus;
import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceCoreProgressTrackerService {

    private final Map<BuildInfo, ProgressStatus> progressMap = new ConcurrentHashMap<>();

    public ProgressStatus updateProgress(BuildInfo buildInfo, ProgressStatus progressStatus) {
        return progressMap.put(buildInfo, progressStatus);
    }

    public Map<BuildInfo, ProgressStatus> getProgress() {
        return Map.copyOf(progressMap);
    }
}
