package com.lllbllllb.productinfoservice.core;

import java.time.Clock;
import java.time.ZonedDateTime;

import com.lllbllllb.productinfoservice.core.model.Products;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ProductInfoServiceCoreBuildsProcessor {

    private final WebClient buildsListClient;

    private final Clock clock;

    private volatile ZonedDateTime lastCheck;

    public ProductInfoServiceCoreBuildsProcessor(
        WebClient buildsListClient,
        Clock clock
    ) {
        this.buildsListClient = buildsListClient;
        this.clock = clock;
        lastCheck = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, clock.getZone());
    }

    public void process() {
        System.out.println("----");

        buildsListClient.get()
            .ifModifiedSince(lastCheck)
            .retrieve()
            .onStatus(HttpStatus::is3xxRedirection, clientResponse -> {
                System.out.println(clientResponse.statusCode());

                return Mono.empty();
            })
            .bodyToMono(Products.class)
            .subscribe(body -> {
                System.out.println(body);

                lastCheck = ZonedDateTime.now(clock);
            });
    }
}
