package com.lllbllllb.productinfoservice.model;

/**
 * Attach {@link BuildInfo} to another data.
 */
public record BuildInfoAware<T>(
    BuildInfo buildInfo,
    T obj
) {

}
