package com.justinaji.kafkaproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProducerConfig {

        @Bean
        public NewTopic creaTopic(){
            return new NewTopic("newtopic", 3, (short)1);
        }
}
