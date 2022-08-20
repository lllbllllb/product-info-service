package com.lllbllllb.productinfoservice.core;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceCoreInstanceService {

    private volatile String instanceId;

    public String getInstanceId() {
        if (instanceId == null) {
            synchronized (ProductInfoServiceCoreInstanceService.class) {
                if (instanceId == null) {
                    instanceId = UUID.randomUUID().toString();
                }
            }
        }

        return instanceId;
    }
}
