package com.lllbllllb.productinfoservice.core;

import java.time.Clock;
import java.util.List;

import com.lllbllllb.productinfoservice.core.handler.ProductInfoServiceCoreHttpRequestHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.netty.http.client.HttpClient;

import static com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreHttpRoutes.BUILD_NUMBER_URL;
import static com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreHttpRoutes.PRODUCT_CODE_URL;
import static com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreHttpRoutes.REFRESH_PRODUCT_CODE_URL;
import static com.lllbllllb.productinfoservice.core.ProductInfoServiceCoreHttpRoutes.REFRESH_URL;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XHTML_XML;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_XML;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@ComponentScan(basePackageClasses = ProductInfoServiceCoreAutoConfiguration.class)
public class ProductInfoServiceCoreAutoConfiguration {

    @Bean
    RouterFunction<ServerResponse> routes(ProductInfoServiceCoreHttpRequestHandler handler) {
        return route(GET("/"), handler::root)
            .and(route(GET("/status"), handler::getStatus))
            .and(route(POST(REFRESH_URL), handler::refresh))
            .and(route(POST(REFRESH_PRODUCT_CODE_URL), handler::refreshByCode))
            .and(route(GET(PRODUCT_CODE_URL), handler::getProductCode))
            .and(route(GET(BUILD_NUMBER_URL), handler::getBuildNumber));
    }

    @Bean
    WebClient updatesXmlClient(
        WebClient.Builder webClientBuilder,
        ProductInfoServiceCoreConfigurationProperties properties
    ) {
        return webClientBuilder.clone()
            .baseUrl(properties.getUpdatesXmlUrl())
            .defaultHeaders(httpHeaders -> httpHeaders.setAccept(List.of(TEXT_XML, APPLICATION_XML, APPLICATION_XHTML_XML)))
            .build();
    }

    @Bean
    WebClient releasesCodeClient(
        WebClient.Builder webClientBuilder,
        ProductInfoServiceCoreConfigurationProperties properties
    ) {
        return webClientBuilder.clone()
            .baseUrl(properties.getReleasesCodeUrl())
            .defaultHeaders(httpHeaders -> httpHeaders.setAccept(List.of(APPLICATION_JSON)))
            .build();
    }

    @Bean
    WebClient redirectedWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.clone()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create().followRedirect(true)
            ))
            .build();
    }

    @Bean
    @ConditionalOnMissingBean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
