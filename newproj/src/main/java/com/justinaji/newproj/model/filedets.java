package com.justinaji.newproj.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor //makes the constructor and neccessary get methods for the class automatically
public class filedets {
    public int id;
    public String name;
    public String type;

}
