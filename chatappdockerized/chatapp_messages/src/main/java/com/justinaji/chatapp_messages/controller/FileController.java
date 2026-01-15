package com.justinaji.chatapp_messages.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.justinaji.chatapp_messages.dto.FileDownloadDto;
import com.justinaji.chatapp_messages.dto.Filedata;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.service.FileHandlerServices;
import com.justinaji.chatapp_messages.service.KafkaProducerServices;



@RestController
@RequestMapping("/files")
public class FileController {
    
    private final KafkaProducerServices kafkaProducerServices;
    private final WebClient TempMsgWebClient;
    private final FileHandlerServices fileHandlerServices;

    public FileController(
        @Qualifier("TempMsgWebClient") WebClient TempMsgWebClient, 
        FileHandlerServices fileHandlerServices,
        KafkaProducerServices kafkaProducerServices) {
        this.fileHandlerServices = fileHandlerServices;
        this.TempMsgWebClient = TempMsgWebClient;
        this.kafkaProducerServices = kafkaProducerServices;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("data") Filedata filedata) throws IOException {
        if (file.isEmpty())  return ResponseEntity.badRequest().body("File is empty");

        media upload = fileHandlerServices.UploadFile(file, filedata.getSender(), filedata.getChatid());
        return ResponseEntity.status(HttpStatus.OK).body(upload);
    }
    
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam String fileid,@RequestParam(required = false, defaultValue = "false") boolean inline) throws IOException {

        FileDownloadDto fileData = fileHandlerServices.DownloadFile(fileid);
        String disposition = inline ? "inline" : "attachment";
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(fileData.getFiletype());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        String encodedFilename = URLEncoder.encode(
            fileData.getFilename(),
            StandardCharsets.UTF_8
        ).replace("+", "%20");

        return ResponseEntity.ok()
            .contentType(mediaType)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                disposition +"; filename=\"" + encodedFilename + "\"")
            .body(fileData.getBytes());
    }
    
}
