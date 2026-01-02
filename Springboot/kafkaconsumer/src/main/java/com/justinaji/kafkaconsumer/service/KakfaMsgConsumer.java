package com.justinaji.kafkaconsumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KakfaMsgConsumer {

    Logger logger = LoggerFactory.getLogger(KakfaMsgConsumer.class);

    @KafkaListener(topics = "newtopic", groupId= "consumer-group")
    public void consume(String msg){
        logger.info("consumer consumed the message: "+ msg);
    }
}   
