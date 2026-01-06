package com.justinaji.kafkaproducer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.justinaji.kafkaproducer.model.MessageRequest;
import com.justinaji.kafkaproducer.service.KafkaMessagePublisher; 

@RestController
public class EventController {

    @Autowired
    public KafkaMessagePublisher publisher;

    @PostMapping("/publish/")
    public ResponseEntity<?> publishMessage(@RequestBody MessageRequest msgreq) {
        try {
            publisher.SendMsg2Topic(msgreq.getKey(),msgreq.getMessage());
            return ResponseEntity.ok("Message published (Springboot)");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
}

