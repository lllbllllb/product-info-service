package com.lllbllllb.productinfoservice.core.model;

public record BuildInfoAware<T>(
    BuildInfo buildInfo,
    T obj
) {

}
