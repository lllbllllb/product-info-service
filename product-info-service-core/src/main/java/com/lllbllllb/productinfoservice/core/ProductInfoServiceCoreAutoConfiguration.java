package com.lllbllllb.productinfoservice.core;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lllbllllb.productinfoservice.core.model.FileExtractorLabel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@ComponentScan
public class ProductInfoServiceCoreAutoConfiguration {

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

    @Bean
    Map<FileExtractorLabel, ProductInfoServiceCoreFileExtractor> labelToFileExtractorMap(List<ProductInfoServiceCoreFileExtractor> fileExtractors) {
        return fileExtractors.stream()
            .collect(Collectors.toMap(ProductInfoServiceCoreFileExtractor::getLabel, Function.identity()));
    }
}
