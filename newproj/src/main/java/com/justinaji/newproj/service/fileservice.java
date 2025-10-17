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

    public String getfiledets(){
        try{
            String details="Documents present: \n"; 
            if (userservice.loggedin){
                for (filedets f : files){
                    details+= " ("+f.getId()+") "+ f.getName() +"."+f.getType()+",\n ";
                } 
                return details;}
            else throw new NoUserFound(); 
        }
        catch(Exception e){ return e.getMessage(); }
    }

    public String filebyid(int id) {
        try{
            for (filedets f : files){
                if (f.getId() == id && userservice.loggedin) { 
                    return "Document details- id: "+f.getId()+", Name: "+f.getName()+", Document type: "+f.getType(); 
                }
            }
            throw new NoUserFound();
        }
        catch(Exception e){ return e.getMessage();}
    }

    public boolean addfile(filedets file){ 
        if( userservice.loggedin){
            files.add(file);
            return true;
        } 
        return false;
    }

    public String rename(int id, String name) {
        try{
            String oldname = "";
            for (filedets f : files){
                if (f.getId() == id && userservice.loggedin) {
                    oldname = f.name;
                    f.name = name;
                    return"Name changed from "+oldname+" to "+name;
                }
            }
            throw new NoUserFound();
        }
        catch(Exception e){ return e.getMessage();}
    }

    public String delete(int id) {//delete by id 
        try{
            for (filedets f : files){
                if (f.getId() == id) {
                    String s = "Removed file '"+f.name+"' from the list ";
                    files.remove(f);
                    return s; 
                }
            }
            throw new NoUserFound();
        }
        catch(Exception e){ return e.getMessage();}
    }

    public String delete(String name) { //delete by name
        try{
            for (filedets f : files){
                if (f.getName().equals(name)) {
                    String s = "Removed file '"+f.name+"' from the list ";
                    files.remove(f);
                    return s; 
                }
            }
           throw new NoUserFound();
        }
        catch(Exception e){ return e.getMessage();}
    }
}
