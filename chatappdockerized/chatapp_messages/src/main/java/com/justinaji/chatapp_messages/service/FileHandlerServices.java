package com.justinaji.chatapp_messages.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.justinaji.chatapp_messages.dto.FileDownloadDto;
import com.justinaji.chatapp_messages.model.media;


@Service
public class FileHandlerServices {

    @Value("${storage.path}")
    private String storagePath;

    Logger logger = LoggerFactory.getLogger(MessageServicesImpl.class);

    private final WebClient MediaWebClient;

    public FileHandlerServices(@Qualifier("MediaWebClient") WebClient MediaWebClient){ this.MediaWebClient = MediaWebClient;
}

    public media UploadFile(MultipartFile file,String sender,String chatid) throws IOException{
        
        String filepath = storagePath + file.getOriginalFilename();
        String Fileid;
        List<String> ids = MediaWebClient.get()
        .uri("/media/idlist")
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .bodyToFlux(JsonNode.class)
        .map(node -> node.get("fileid").asText())
        .collectList()
        .block();
        do { Fileid = CommonMethods.getAlphaNumericString(); } // unique chat id
        while (ids.contains(ids));
        media newFile = new media(Fileid, filepath, sender, file.getOriginalFilename(), file.getContentType(), chatid, Timestamp.from(Instant.now()), false);
        SendMediatoTopic(newFile);

        Path dir = Paths.get(storagePath);
        Files.createDirectories(dir); 
        file.transferTo(new File(filepath));

        return newFile;
    }

    public FileDownloadDto DownloadFile(String fileid) throws IOException {
        media mediaFile = MediaWebClient.get()
            .uri("/media/getfile?id=" + fileid)
            .retrieve()
            .bodyToMono(media.class)
            .block();
        if (mediaFile == null) throw new RuntimeException("file not found!");

        String filepath = mediaFile.getPath();
        byte[] fileBytes = Files.readAllBytes(Paths.get(filepath));

        return new FileDownloadDto( fileBytes, mediaFile.getFiletype(), mediaFile.getName());
    }

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
    
}
