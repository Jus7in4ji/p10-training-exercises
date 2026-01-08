package com.justinaji.chatapp_messages.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.justinaji.chatapp_messages.dto.Filedata;
import com.justinaji.chatapp_messages.model.media;
import com.justinaji.chatapp_messages.service.FileHandlerServices;


@RestController
@RequestMapping("/files")
public class FileController {

    private final FileHandlerServices fileHandlerServices;

    public FileController(FileHandlerServices fileHandlerServices) {
        this.fileHandlerServices = fileHandlerServices;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestPart("file") MultipartFile file, @RequestPart("data") Filedata filedata) throws IOException {
        if (file.isEmpty())  return ResponseEntity.badRequest().body("File is empty");
        media upload = fileHandlerServices.UploadFile(file, filedata.getSender(), filedata.getChatid());
        
        return ResponseEntity.status(HttpStatus.OK).body(upload);
    }
    

}
