package com.justinaji.newproj.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import com.justinaji.newproj.model.filedets;
@Service
public class fileservice {
    List<filedets> files =  new ArrayList<>(Arrays.asList( //initialize sample data 
        new filedets(1,"sample1","pdf"),
        new filedets(2,"page0","pdf"),
        new filedets(3,"workerlist","docx")));

    public List<filedets> getfiledets(){ return files; }

    public filedets filebyid(int id) {
        for (filedets f : files){
            if (f.getId() == id) { return f; }
        }
        return null; // or throw an exception if not found
    }

    public void addfile(filedets file){ files.add(file); }

    public void rename(int id, String name) {
        for (filedets f : files){
            if (f.getId() == id) {
                System.out.print("Name changed from "+f.name+" to "+name);
                f.name = name;
            }
        }
    }

    public String delete(int id) {//delete by id 
        for (filedets f : files){
            if (f.getId() == id) {
                String s = "Removed file '"+f.name+"' from the list ";
                files.remove(f);
                return s; 
            }
        }
        return "file of the given id not found";
    }

    public String delete(String name) { //delete by name
        for (filedets f : files){
            if (f.getName().equals(name)) {
                String s = "Removed file '"+f.name+"' from the list ";
                files.remove(f);
                return s; 
            }
        }
        return "file of the given name not found";
    }
}
