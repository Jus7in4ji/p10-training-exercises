package com.justinaji.newproj.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.justinaji.newproj.exception.NoUserFound;
import com.justinaji.newproj.model.filedets;

@Service
public class fileservice {
    List<filedets> files =  new ArrayList<>(Arrays.asList( //initialize sample data 
        new filedets(1,"sample1","pdf"),
        new filedets(2,"page0","pdf"),
        new filedets(3,"workerlist","docx")));

    public String getfiledets(){
        String details="Documents present: "; 
        if (userservice.loggedin){
            for (filedets f : files){
                details+= "\n ("+f.getId()+") "+ f.getName() +"."+f.getType();
            } 
            return details;}
        else throw new NoUserFound(); 
    }

    public String filebyid(int id) {
        if(userservice.loggedin){
            for (filedets f : files){
                if (f.getId() == id) { 
                    return "Document details- \n id: "+f.getId()+"\n Name: "+f.getName()+"\n Document type: "+f.getType(); 
                }
            }
            return "No document found with given id";
        }
        throw new NoUserFound();
    }

    public boolean addfile(filedets file){ 
        if( userservice.loggedin){
            if(file.getName()== null || file.getType() == null){
                return false;
            }
            int newid=0;
            for(filedets f: files){
                if(newid <=f.id) newid = f.id+1;
            }
            file.id = newid;
            files.add(file);
            return true;
        } 
        return false;
    }

    public String updatefile(int id, String name) {
        if(userservice.loggedin){
            String oldname = "";
            for (filedets f : files){
                if (f.getId() == id) {
                    oldname = f.name;
                    f.name = name;
                    return"Name changed from "+oldname+" to "+name;
                }
            }
            return "File not found";
        }
        throw new NoUserFound();
    }

    public String delete(int id) {//delete by id 
        if(userservice.loggedin){
            for (filedets f : files){
                if (f.getId() == id) {
                    String s = "Removed file '"+f.name+"' from the list ";
                    files.remove(f);
                    return s; 
                }
            }
            return "No file of given id found";
        }
        throw new NoUserFound();
    }

    public String delete(String name) { //delete by name
        if(userservice.loggedin){
            for (filedets f : files){
                if (f.getName().equals(name)) {
                    String s = "Removed file '"+f.name+"' from the list ";
                    files.remove(f);
                    return s; 
                }
            }
            return "No file of given id found";
        }
        throw new NoUserFound();
    }
}