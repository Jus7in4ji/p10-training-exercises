package com.justinaji.chatapp_messages.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

import com.fasterxml.jackson.databind.JsonNode;
import com.justinaji.chatapp_messages.dto.FileDownloadDto;
import com.justinaji.chatapp_messages.dto.Filedata;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.service.FileHandlerServices;



@RestController
@RequestMapping("/files")
public class FileController {

    private final WebClient MediaWebClient;
    private final FileHandlerServices fileHandlerServices;

    public FileController(@Qualifier("MediaWebClient") WebClient MediaWebClient, FileHandlerServices fileHandlerServices) {
        this.fileHandlerServices = fileHandlerServices;
        this.MediaWebClient = MediaWebClient;
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
        List<String> ids = MediaWebClient.get()
            .uri("/media/idlist")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(JsonNode.class)
            .map(node -> node.get("fileid").asText())
            .collectList().block();

        if (ids == null) throw new RuntimeException("Chat ID not found!");
        return ResponseEntity.status(HttpStatus.OK).body(ids.contains(chatid));
    }
    
}
