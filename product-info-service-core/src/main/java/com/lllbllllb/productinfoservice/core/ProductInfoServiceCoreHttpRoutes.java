package com.lllbllllb.productinfoservice.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductInfoServiceCoreHttpRoutes {

    public static final String PRODUCT_CODE = "productCode";

    public static final String PRODUCT_CODE_URL = String.format("/{%s}", PRODUCT_CODE);

    public static final String BUILD_NUMBER = "buildNumber";

    public static final String BUILD_NUMBER_URL = String.format("/{%s}/{%s}", PRODUCT_CODE, BUILD_NUMBER);

    public static final String REFRESH_URL = "/refresh";

    public static final String REFRESH_PRODUCT_CODE_URL = String.format("%s%s", REFRESH_URL, PRODUCT_CODE_URL);
}
