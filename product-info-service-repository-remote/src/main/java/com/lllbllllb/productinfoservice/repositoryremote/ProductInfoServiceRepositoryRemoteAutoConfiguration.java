package com.lllbllllb.productinfoservice.repositoryremote;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_XML;

@ComponentScan
public class ProductInfoServiceRepositoryRemoteAutoConfiguration {

    @Bean
    WebClient updatesXmlClient(
        WebClient.Builder webClientBuilder,
        ProductInfoServiceRepositoryRemoteConfigurationProperties properties
    ) {
        return webClientBuilder.clone()
            .baseUrl(properties.getUpdatesXmlUrl())
            .defaultHeaders(httpHeaders -> httpHeaders.setAccept(List.of(APPLICATION_XML)))
            .build();
    }

    @Bean
    WebClient releasesCodeClient(
        WebClient.Builder webClientBuilder,
        ProductInfoServiceRepositoryRemoteConfigurationProperties properties
    ) {
        return webClientBuilder.clone()
            .baseUrl(properties.getReleasesCodeUrl())
            .defaultHeaders(httpHeaders -> httpHeaders.setAccept(List.of(APPLICATION_JSON)))
            .build();
    }

    @Bean
    WebClient commonGuestClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.clone().build();
    }
}
