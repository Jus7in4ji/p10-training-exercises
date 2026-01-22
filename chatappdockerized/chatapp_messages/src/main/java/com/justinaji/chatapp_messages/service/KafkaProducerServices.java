package com.justinaji.chatapp_messages.service;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
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
            if (ex ==null) logger.info("Sent File details [ "+ file.getName() +"]");
            else System.out.println("Unable to Send Message("+file.getName()+") due to : "+ex.getMessage());
            
        });
    }

    public void SetFileRead(String fileid){
        CompletableFuture<SendResult<String, Object>> future = template.send("fileread", fileid);
        future.whenComplete((result,ex)->{
            if (ex ==null) logger.info( "Sent request to set file [ "+ fileid+"] as read ");
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

    @KafkaListener(topics = "fileack", groupId= "consumer-group")
    public void consume(String filename){
        logger.info("file ["+ filename+"] received.");
    }

    @KafkaListener(topics = "newid", groupId= "consumer-group")
    public void consume2(String payload) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        media file = mapper.readValue(payload, media.class);
        
        if(file.getSenttime()!=null){
            Instant sentInstant = Instant.parse(file.getSenttime());
            Instant now = Instant.now();

            if (Duration.between(sentInstant, now).getSeconds() > 5) {
                logger.info("File is older than 5 seconds");
                
            }
        }
        else file.setSenttime(Timestamp.from(Instant.now()).toString());
        file.setFileid("hijk");

        logger.info("set new fileid for file ["+file.getName()+"]");
        SendMediatoTopic(file);
    }
}
