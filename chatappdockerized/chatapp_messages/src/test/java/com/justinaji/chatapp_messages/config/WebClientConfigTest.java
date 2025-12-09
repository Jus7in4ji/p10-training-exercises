package com.justinaji.chatapp_messages.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootTest(classes = WebClientConfig.class)
@TestPropertySource(properties = {
        "userchats.service.url=http://localhost:8082"
})
class WebClientConfigTest {

    @Autowired
    private WebClient webClient;

    @Test
    void WebClientBeanCreation() {
        assertThat(webClient).isNotNull();
    }
}
