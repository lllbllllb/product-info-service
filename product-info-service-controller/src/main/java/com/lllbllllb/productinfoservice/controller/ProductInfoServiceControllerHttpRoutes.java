package com.lllbllllb.productinfoservice.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductInfoServiceControllerHttpRoutes {

    public static final String PRODUCT_CODE = "productCode";

    public static final String PRODUCT_CODE_URL = "/code/{%s}".formatted(PRODUCT_CODE);

    public static final String BUILD_NUMBER = "buildNumber";

    public static final String PRODUCT_CODE_BUILD_NUMBER_URL = "/code/{%s}/build/{%s}".formatted(PRODUCT_CODE, BUILD_NUMBER);

    public static final String REFRESH_URL = "/refresh";

    public static final String STATUS_URL = "/status";

    public static final String REFRESH_PRODUCT_CODE_URL = "%s%s".formatted(REFRESH_URL, PRODUCT_CODE_URL);

    public static final String LAST_BUILDS_DATA = "/lastBuildsData";

    public static final String ACTIVE_ROUNDS_DATA = "/activeRoundsData";
}
