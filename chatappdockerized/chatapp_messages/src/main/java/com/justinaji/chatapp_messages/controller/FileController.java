package com.justinaji.chatapp_messages.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
import com.justinaji.chatapp_messages.dto.TempMsg;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.repository.MessageRepo;
import com.justinaji.chatapp_messages.service.CommonMethods;
import com.justinaji.chatapp_messages.service.FileHandlerServices;



@RestController
@RequestMapping("/files")
public class FileController {
    
    private final MessageRepo messageRepo;
    private final WebClient TempMsgWebClient;
    private final FileHandlerServices fileHandlerServices;

    public FileController(
        @Qualifier("TempMsgWebClient") WebClient TempMsgWebClient, 
        FileHandlerServices fileHandlerServices,
        MessageRepo messageRepo) {
        this.fileHandlerServices = fileHandlerServices;
        this.TempMsgWebClient = TempMsgWebClient;
        this.messageRepo = messageRepo;
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
    
    @GetMapping("/test")
    public ResponseEntity<?> getMethodName(@RequestParam String chatid) {
        System.out.println("passing chatid: "+chatid);
        List<TempMsg> temps = TempMsgWebClient.get()
            .uri("/temp/getall")
            .retrieve()
            .bodyToFlux(TempMsg.class)
            .collectList().block();
        if (temps == null) throw new RuntimeException("Temp messages not found!");
        DateTimeFormatter dbFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
        temps.forEach(msg->{
            String ts = msg.getSenttime().toString().substring(0, 21).replace("T", " ");
            
            LocalDateTime ldt = LocalDateTime.parse(ts,dbFormat);
            msg.setFormattedtime(CommonMethods.formatTimestamp(Timestamp.valueOf(ldt),"IST"));
        });
        return ResponseEntity.status(HttpStatus.OK).body(temps);
    }
    
}
