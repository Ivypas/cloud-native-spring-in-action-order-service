package com.polarbookshop.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    /**
     * If you use IntelliJ IDEA, you might get a warning that WebClient.Builder cannot be autowired.
     * Don’t worry. It’s a false positive.
     * You can get rid of the warning by annotating the field
     * with @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection").
     *
     * @param clientProperties
     * @param webClientBuilder
     * @return
     */
    @Bean
    WebClient webClient(ClientProperties clientProperties, WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(clientProperties.catalogServiceUri().toString())
                .build();
    }

    /* @Bean
    WebClient webClient2(ClientProperties clientProperties) {
        return WebClient.builder()
                .baseUrl(clientProperties.catalogServiceUri().toString())
                .build();
    } */

}
