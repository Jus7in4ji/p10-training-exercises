package com.justinaji.newproj.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.justinaji.newproj.exception.NoUserFound;
import com.justinaji.newproj.model.filedets;
import com.justinaji.newproj.repo.repofile;

@Service
public class fileservice {

    @Autowired
    repofile frepo;

    // ====== Get All Files ======
    public String getfiledets() {
        if (userservice.loggedin) {
            List<filedets> files = frepo.findAll();
            if (files.isEmpty()) {
                return "No documents present in the database.";
            }

            StringBuilder details = new StringBuilder("Documents present:");
            for (filedets f : files) {
                details.append("\n (")
                       .append(f.getId())
                       .append(") ")
                       .append(f.getName())
                       .append(".")
                       .append(f.getType());
            }
            return details.toString();
        } else {
            throw new NoUserFound();
        }
    }

    // ====== Get File by ID ======
    public String filebyid(String id) {
        if (userservice.loggedin) {
            Optional<filedets> file = frepo.findById(id);
            if (file.isPresent()) {
                filedets f = file.get();
                return "Document details:\n"
                     + "id: " + f.getId() + "\n"
                     + "Name: " + f.getName() + "\n"
                     + "Document type: " + f.getType();
            }
            return "No document found with given id.";
        }
        throw new NoUserFound();
    }

    // ====== Add New File ======
    public boolean addfile(filedets file) {
        if (userservice.loggedin) {
            if (file.getName() == null || file.getType() == null) {
                return false;
            }
            String randomId;
            do {
                randomId = CommonMethods.getAlphaNumericString();
            } while (frepo.existsById(randomId)); // ensure uniqueness

            file.setId(randomId);
            frepo.save(file);
            return true;
        }
        return false;
    }

    // ====== Update File Name by ID ======
    public String updatefile(String id, String name) {
        if (userservice.loggedin) {
            Optional<filedets> optionalFile = frepo.findById(id);
            if (optionalFile.isPresent()) {
                filedets f = optionalFile.get();
                String oldName = f.getName();
                f.setName(name);
                frepo.save(f); // update in DB
                return "Name changed from " + oldName + " to " + name;
            }
            return "File not found.";
        }
        throw new NoUserFound();
    }

    // ====== Delete File by ID ======
    public String delete(String id) {
        if (userservice.loggedin) {
            Optional<filedets> optionalFile = frepo.findById(id);
            if (optionalFile.isPresent()) {
                filedets f = optionalFile.get();
                frepo.deleteById(id);
                frepo.flush();
                return "Removed file '" + f.getName() + "' from the list.";
            }
            return "No file of given id found.";
        }
        throw new NoUserFound();
    }

   /*  // ====== Delete File by Name ======
    public String delete(String name) {
        if (userservice.loggedin) {
            List<filedets> files = frepo.findAll();
            for (filedets f : files) {
                if (f.getName().equalsIgnoreCase(name)) {
                    frepo.delete(f);
                    return "Removed file '" + f.getName() + "' from the list.";
                }
            }
            return "No file with given name found.";
        }
        throw new NoUserFound();
    }*/
}
