package com.justinaji.kafkaproducer.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessagePublisher {

    @Autowired
    private KafkaTemplate<String,Object> template;

    public void SendMsg2Topic(String key, String Msg){
        CompletableFuture<SendResult<String, Object>> future = template.send("newtopic",key, Msg);
        future.whenComplete((result,ex)->{
            if (ex ==null) System.out.println(
                "Sent Message : "+ Msg +
                "\n-with offset : "+result.getRecordMetadata().offset()+ 
                "\n- partition: " + result.getRecordMetadata().partition());
            
            else System.out.println("Unable to Send Message("+Msg+") due to : "+ex.getMessage());
            
        });
    }
}
