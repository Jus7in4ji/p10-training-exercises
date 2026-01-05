package com.justinaji.kafkaproducer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.kafkaproducer.service.KafkaMessagePublisher;


@RestController
public class EventController {

    @Autowired
    public KafkaMessagePublisher publisher;

    @GetMapping("/publish/{key}/{message}")
    public ResponseEntity<?> publishMessage(
            @PathVariable String key,@PathVariable String message) {
        try {
            publisher.SendMsg2Topic(key,message);
            return ResponseEntity.ok("Message published (Springboot)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}
