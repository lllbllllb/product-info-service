package com.lllbllllb.productinfoservice.model;

import java.time.Instant;

/**
 * Data about scan rounds.
 */
public record Round(
    String instanceId,
    Instant cratedDate
) {

}
