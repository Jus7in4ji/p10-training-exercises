package com.justinaji.chatapp_messages.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.justinaji.chatapp_messages.dto.FileDownloadDto;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.repository.MediaRepo;


@Service
public class FileHandlerServices {

    private final MediaRepo mediaRepo;

    @Value("${storage.path}")
    private String storagePath;

    Logger logger = LoggerFactory.getLogger(MessageServicesImpl.class);

    public FileHandlerServices(MediaRepo mediaRepo){ this.mediaRepo = mediaRepo;}

    public media UploadFile(MultipartFile file,String sender,String chatid) throws IOException{
        
        String filepath = storagePath + file.getOriginalFilename();
        String Fileid;

        do { Fileid = CommonMethods.getAlphaNumericString(); } // unique chat id
        while (mediaRepo.existsById(Fileid));
        media newFile = new media(Fileid, filepath, sender, file.getOriginalFilename(), file.getContentType(), chatid, Timestamp.from(Instant.now()), false);
        mediaRepo.save(newFile);
        SendMediatoTopic(newFile);

        Path dir = Paths.get(storagePath);
        Files.createDirectories(dir); 
        file.transferTo(new File(filepath));

        return newFile;
    }

    public FileDownloadDto DownloadFile(String fileid) throws IOException {
        media mediaFile = mediaRepo.findById(fileid).orElseThrow(() -> new RuntimeException("File not found"));

        String filepath = mediaFile.getPath();
        byte[] fileBytes = Files.readAllBytes(Paths.get(filepath));

        return new FileDownloadDto( fileBytes, mediaFile.getFiletype(), mediaFile.getName());
    }

    @Autowired
    private KafkaTemplate<String,Object> template;

    public void SendMediatoTopic(media file){
        CompletableFuture<SendResult<String, Object>> future = template.send("MediaTopic",file.getChatid(), file);
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
    
}
