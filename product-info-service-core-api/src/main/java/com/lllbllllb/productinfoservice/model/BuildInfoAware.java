package com.lllbllllb.productinfoservice.model;

public record BuildInfoAware<T>(
    BuildInfo buildInfo,
    T obj
) {

}
