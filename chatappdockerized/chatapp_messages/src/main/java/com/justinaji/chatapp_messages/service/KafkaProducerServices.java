package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.justinaji.chatapp_messages.dto.TempMsg;
import com.justinaji.chatapp_messages.model.media;

@Service
public class KafkaProducerServices {

    Logger logger = LoggerFactory.getLogger(KafkaProducerServices.class);

    @Autowired
    private KafkaTemplate<String,Object> template;

    public void SendMediatoTopic(media file){
        CompletableFuture<SendResult<String, Object>> future = template.send("MediaTopic0",file.getFiletype(), file);
        future.whenComplete((result,ex)->{
            if (ex ==null) logger.info(
                "Sent Message [ "+ file.toString() +
                "] with offset ["+result.getRecordMetadata().offset()+ 
                "] to partition [" + result.getRecordMetadata().partition()+"]");
            
            else System.out.println("Unable to Send Message("+file.getName()+") due to : "+ex.getMessage());
            
        });
    }

    public void SetFileRead(String fileid){
        CompletableFuture<SendResult<String, Object>> future = template.send("fileread", fileid);
        future.whenComplete((result,ex)->{
            if (ex ==null) logger.info(
                "Sent Message [ "+ fileid+
                "] with offset ["+result.getRecordMetadata().offset()+ 
                "] to partition [" + result.getRecordMetadata().partition()+"]");
            
            else System.out.println("Unable to Set to read ("+fileid+") due to : "+ex.getMessage());
            
        });
    }

    public void StoreTempmsg(TempMsg temp){
        temp.setFormattedtime(Timestamp.from(Instant.now()).toString());
        CompletableFuture<SendResult<String, Object>> future = template.send("tempmsg0",temp.getChatid(), temp);
        future.whenComplete((result,ex)->{
            if (ex ==null) logger.info(
                "Sent Message [ "+ temp.toString() +
                "] with offset ["+result.getRecordMetadata().offset()+ 
                "] to partition [" + result.getRecordMetadata().partition()+"]");
            
            else System.out.println("Unable to Send Message("+temp.getMessage()+") due to : "+ex.getMessage());
            
        });
    }
}
