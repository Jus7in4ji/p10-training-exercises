package com.justinaji.jproj.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class chatdetails {

    private String chatname;
    private String creator;
    private int member_count;
    private Set<String> members;

}
