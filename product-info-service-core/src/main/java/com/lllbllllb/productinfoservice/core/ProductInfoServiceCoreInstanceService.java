package com.lllbllllb.productinfoservice.core;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class ProductInfoServiceCoreInstanceService {

    public String getInstanceId() {
        return UUID.randomUUID().toString();
    }
}
