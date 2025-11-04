package com.justinaji.usemysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileAdminDTO {
    private String id;
    private String name;
    private String type;
    private String uploaderId;
    private String uploaderName;
}
