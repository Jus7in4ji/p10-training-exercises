package com.justinaji.chatapp_messages.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.repository.MediaRepo;


@Service
public class FileHandlerServices {

    private final MediaRepo mediaRepo;

    @Value("${storage.path}")
    private String storagePath;

    Logger logger = LoggerFactory.getLogger(MessageServicesImpl.class);

    
    public FileHandlerServices(MediaRepo mediaRepo){
        this.mediaRepo = mediaRepo;
    }

    public media UploadFile(MultipartFile file,String sender,String chatid) throws IOException{
        
        String filepath = storagePath + file.getOriginalFilename();
        String Fileid;
        do { Fileid = CommonMethods.getAlphaNumericString(); } // unique chat id
        while (mediaRepo.existsById(Fileid));
        media newFile = new media(Fileid, filepath, sender, file.getOriginalFilename(), file.getContentType(), chatid, Timestamp.from(Instant.now()), false);
        mediaRepo.save(newFile);
        
        Path dir = Paths.get(storagePath);
        Files.createDirectories(dir); 
        file.transferTo(new File(filepath));

        return newFile;
    }

    public byte[] DownloadFile(String fileid)throws  IOException{
        Optional<media> downloadFile = mediaRepo.findById(fileid); 
        String filepath = downloadFile.get().getPath();
        byte[] file = Files.readAllBytes(new File(filepath).toPath());
        return file;
    }
}
