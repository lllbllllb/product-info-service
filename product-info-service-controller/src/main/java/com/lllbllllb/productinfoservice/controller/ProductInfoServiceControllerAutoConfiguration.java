package com.lllbllllb.productinfoservice.controller;

import java.time.Clock;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.BUILD_NUMBER_URL;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.PRODUCT_CODE_URL;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.REFRESH_PRODUCT_CODE_URL;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.REFRESH_URL;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@ComponentScan
public class ProductInfoServiceControllerAutoConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(ProductInfoServiceControllerHttpRequestHandler handler) {
        return route(GET("/status"), handler::getLastBuildInfos)
            .and(route(POST(REFRESH_URL), handler::refresh))
            .and(route(POST(REFRESH_PRODUCT_CODE_URL), handler::refreshByCode))
            .and(route(GET(PRODUCT_CODE_URL), handler::getByProductCode))
            .and(route(GET(BUILD_NUMBER_URL), handler::getByProductCodeAndBuildNumber));
    }
}
