package com.lllbllllb.productinfoservice.controller;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.ACTIVE_ROUNDS_DATA;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.LAST_BUILDS_DATA;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.PRODUCT_CODE_BUILD_NUMBER_URL;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.PRODUCT_CODE_URL;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.REFRESH_PRODUCT_CODE_URL;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.REFRESH_URL;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.STATUS_URL;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;

/**
 * Autoconfiguration for {@code Controller} module.
 */
@EnableScheduling
@ComponentScan
public class ProductInfoServiceControllerAutoConfiguration {

    static final String TAG = "Product Info Service";

    @Bean
    RouterFunction<ServerResponse> routes(
        ProductInfoServiceControllerHttpRequestHandler requestHandler,
        ProductInfoServiceControllerOpenApiHandler openApiHandler
    ) {

        return route().GET(STATUS_URL, requestHandler::getStatus, openApiHandler::getStatus).build()
            .and(route().GET(LAST_BUILDS_DATA, requestHandler::getLastBuildData, openApiHandler::getLastBuildsData).build())
            .and(route().GET(ACTIVE_ROUNDS_DATA, requestHandler::getActiveRoundsData, openApiHandler::getActiveRoundsData).build())
            .and(route().POST(REFRESH_URL, requestHandler::refresh, openApiHandler::refresh).build())
            .and(route().POST(REFRESH_PRODUCT_CODE_URL, requestHandler::refreshByCode, openApiHandler::refreshByCode).build())
            .and(route().GET(PRODUCT_CODE_URL, requestHandler::getByProductCode, openApiHandler::getByProductCode).build())
            .and(route().GET(PRODUCT_CODE_BUILD_NUMBER_URL, requestHandler::getByProductCodeAndBuildNumber, openApiHandler::getByProductCodeAndBuildNumber).build());
    }

    @Bean
    OpenAPI customOpenAPI() {
        var tag = new Tag();
        tag.setName(TAG);
        tag.setDescription("General API");

        return new OpenAPI()
            .info(new Info().title("Product Info Service").description("Api for product-info-service-controller").version("1.0.0"))
            .tags(List.of(tag));
    }
}
