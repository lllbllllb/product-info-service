package com.lllbllllb.productinfoservice.controller;

import com.lllbllllb.productinfoservice.controller.model.ActiveRoundDataDto;
import com.lllbllllb.productinfoservice.controller.model.BuildInfoDto;
import com.lllbllllb.productinfoservice.controller.model.FullStatusDto;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.stereotype.Service;

import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerAutoConfiguration.TAG;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.BUILD_NUMBER;
import static com.lllbllllb.productinfoservice.controller.ProductInfoServiceControllerHttpRoutes.PRODUCT_CODE;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * OpenAPI data generator.
 */
@Service
public class ProductInfoServiceControllerOpenApiHandler {

    public void getStatus(Builder builder) {
        builder
            .tag(TAG)
            .operationId("Get status")
            .response(responseBuilder()
                .responseCode("200").description("OK")
                .content(contentBuilder()
                    .mediaType(APPLICATION_JSON_VALUE)
                    .schema(schemaBuilder().implementation(FullStatusDto.class))
                )
            );
    }

    public void getLastBuildsData(Builder builder) {
        builder
            .tag(TAG)
            .operationId("Get last builds data")
            .response(responseBuilder()
                .responseCode("200").description("OK")
                .content(contentBuilder()
                    .mediaType(APPLICATION_JSON_VALUE)
                    .schema(schemaBuilder().implementation(BuildInfoDto.class))
                )
            );
    }

    public void getActiveRoundsData(Builder builder) {
        builder
            .tag(TAG)
            .operationId("Get active rounds data")
            .response(responseBuilder()
                .responseCode("200").description("OK")
                .content(contentBuilder()
                    .mediaType(APPLICATION_JSON_VALUE)
                    .schema(schemaBuilder().implementation(ActiveRoundDataDto.class))
                )
            );
    }

    public void refresh(Builder builder) {
        builder
            .tag(TAG)
            .operationId("Refresh all")
            .response(responseBuilder()
                .responseCode("200").description("OK")
                .content(contentBuilder()
                    .mediaType(APPLICATION_JSON_VALUE)
                    .schema(schemaBuilder().implementation(BuildInfoDto.class))
                )
            );
    }

    public void refreshByCode(Builder builder) {
        builder
            .tag(TAG)
            .operationId("Refresh by product code")
            .parameter(parameterBuilder().in(PATH).name(PRODUCT_CODE).required(true))
            .response(responseBuilder()
                .responseCode("200").description("OK")
                .content(contentBuilder()
                    .mediaType(APPLICATION_JSON_VALUE)
                    .schema(schemaBuilder().implementation(BuildInfoDto.class))
                )
            );
    }

    public void getByProductCode(Builder builder) {
        builder
            .tag(TAG)
            .operationId("Get all product-info.json by product code")
            .parameter(parameterBuilder().in(PATH).name(PRODUCT_CODE).required(true))
            .response(responseBuilder()
                .responseCode("200").description("OK")
                .responseCode("404").description("Not found")
                .content(contentBuilder().mediaType(APPLICATION_JSON_VALUE))
            );
    }

    public void getByProductCodeAndBuildNumber(Builder builder) {
        builder
            .tag(TAG)
            .operationId("Get unique product-info.json by product code")
            .parameter(parameterBuilder().in(PATH).name(PRODUCT_CODE).required(true))
            .parameter(parameterBuilder().in(PATH).name(BUILD_NUMBER).required(true))
            .response(responseBuilder()
                .responseCode("200").description("OK")
                .responseCode("404").description("Not found")
                .content(contentBuilder().mediaType(APPLICATION_JSON_VALUE))
            );
    }
}
