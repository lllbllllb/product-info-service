package com.lllbllllb.productinfoservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.tools.agent.ReactorDebugAgent;

@SpringBootApplication
public class ProductInfoServiceApplication {

    public static void main(String[] args) {
        ReactorDebugAgent.init();
        SpringApplication.run(ProductInfoServiceApplication.class, args);
    }

}
