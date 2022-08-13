package com.lllbllllb.productinfoservice.core;

import java.time.Clock;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XHTML_XML;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.http.MediaType.TEXT_XML;

@ComponentScan
public class ProductInfoServiceCoreAutoConfiguration {

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
