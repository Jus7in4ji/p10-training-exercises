package com.justinaji.chatapp_messages.dto;

public class FileDownloadDto {
    private byte[] bytes;
    private String filetype;
    private String filename;

    public FileDownloadDto(byte[] bytes, String filetype, String filename) {
        this.bytes = bytes;
        this.filetype = filetype;
        this.filename = filename;
    }

    public byte[] getBytes() { return bytes; }
    public String getFiletype() { return filetype; }
    public String getFilename() { return filename; }
}
