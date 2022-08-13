package com.lllbllllb.productinfoservice.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.lllbllllb.productinfoservice.model.BuildInfo;
import com.lllbllllb.productinfoservice.model.Status;
import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceCoreProgressTrackerService {

    private final Map<BuildInfo, Status> progressMap = new ConcurrentHashMap<>();

    public Status updateProgress(BuildInfo buildInfo, Status status) {
        return progressMap.put(buildInfo, status);
    }

    public Map<BuildInfo, Status> getProgress() {
        return Map.copyOf(progressMap);
    }
}
