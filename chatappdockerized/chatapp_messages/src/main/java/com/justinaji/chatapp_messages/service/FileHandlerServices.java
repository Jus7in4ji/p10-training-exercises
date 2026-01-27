package com.justinaji.chatapp_messages.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    private final KafkaProducerServices kafkaProducerServices;
    private final WebClient MediaWebClient;

    public FileHandlerServices(
        @Qualifier("MediaWebClient") WebClient MediaWebClient,
        KafkaProducerServices kafkaProducerServices){ 
            this.MediaWebClient = MediaWebClient;
            this.kafkaProducerServices = kafkaProducerServices;}

    public media UploadFile(MultipartFile file,String sender,String chatid) throws IOException{
        String filename, filepath , Fileid, randomString;

        List<String> ids = new ArrayList<>();
        randomString = CommonMethods.getAlphaNumericString();
        ids.add(randomString);

        try {
            List<String> used_ids = MediaWebClient.get()
            .uri("/media/idlist")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(JsonNode.class)
            .map(node -> node.get("fileid").asText())
            .collectList().block();

            ids.addAll(used_ids);
        } catch (Exception e) {
            logger.error("Exception occured : "+ e.toString());
        }
       
        do { Fileid = CommonMethods.getAlphaNumericString(); } // generateunique id
        while (ids.contains(Fileid));

        filename = file.getOriginalFilename();
        int lastDot = filename.lastIndexOf('.');
        filepath = storagePath + filename.substring(0, lastDot) +"_"+ randomString+"."+filename.substring(lastDot + 1) ;

        media newFile = new media(Fileid, filepath, sender, filename, file.getContentType(), chatid, Timestamp.from(Instant.now()).toString(), false);
        kafkaProducerServices.SendMediatoTopic(newFile);

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

        
        logger.info("downloaded file ["+mediaFile.getName()+"]");
        return new FileDownloadDto( fileBytes, mediaFile.getFiletype(), mediaFile.getName());
    }
    
}
