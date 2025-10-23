package com.justinaji.usemysql.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.justinaji.usemysql.dto.FileAdminDTO;
import com.justinaji.usemysql.dto.FileDTO;
import com.justinaji.usemysql.exception.NoUserFound;
import com.justinaji.usemysql.model.filedets;
import com.justinaji.usemysql.model.users;
import com.justinaji.usemysql.repo.repofile;
import com.justinaji.usemysql.repo.repouser;

@Service
public class fileservice {

    private final repofile frepo;
    private final repouser urepo;

    public fileservice(repofile frepo, repouser urepo) {
        this.frepo = frepo;
        this.urepo = urepo;
    }

    public List<?> getfiledets() {
        if (!userservice.loggedin) throw new NoUserFound();

        List<filedets> files = frepo.findAll();

        // ✅ For non-admin users: Only show their files (filtered via stream)
        if (!userservice.isadmin) {
            return files.stream()
                    .filter(f -> f.getUploader().getId().equals(userservice.current_user))
                    .map(f -> new FileDTO(f.getId(), f.getName(), f.getType()))
                    .collect(Collectors.toList());
        }

        // ✅ For admin: Return files with uploader details
        return files.stream()
                .map(f -> new FileAdminDTO(
                        f.getId(),
                        f.getName(),
                        f.getType(),
                        f.getUploader().getId(),
                        f.getUploader().getName()
                ))
                .collect(Collectors.toList());
    }

    public String filebyid(String id) {
        if (userservice.loggedin) {
            return frepo.findById(id)
                    .map(f -> {
                        if (!userservice.isadmin && !f.getUploader().getId().equals(userservice.current_user)) {
                            return "Access denied";
                        }
                        return "Document details:\n"
                                + "id: " + f.getId() + "\n"
                                + "Name: " + f.getName() + "\n"
                                + "Document type: " + f.getType();
                    })
                    .orElse("No document found with given id.");
        }
        throw new NoUserFound();
    }

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

            users currentUser = urepo.findById(userservice.current_user).orElse(null);
            file.setUploader(currentUser);
            frepo.save(file);
            return true;
        }
        throw new NoUserFound();
    }

    public String updatefile(String id, String name) {
        if (userservice.loggedin) {
            return frepo.findById(id)
                    .map(f -> {
                        if (!userservice.isadmin && !f.getUploader().getId().equals(userservice.current_user)) {
                            return "Access denied: You can only rename your own files.";
                        }
                        String oldName = f.getName();
                        f.setName(name);
                        frepo.save(f);
                        return "Name changed from " + oldName + " to " + name;
                    })
                    .orElse("File not found.");
        }
        throw new NoUserFound();
    }

    public String delete(String id) {
        if (userservice.loggedin) {
            return frepo.findById(id)
                    .map(f -> {
                        if (!userservice.isadmin && !f.getUploader().getId().equals(userservice.current_user)) {
                            return "Access denied: You can only delete your own files.";
                        }
                        frepo.deleteById(id);
                        frepo.flush();
                        return "Removed file '" + f.getName() + "' from the list.";
                    })
                    .orElse("No file of given id found.");
        }
        throw new NoUserFound();
    }
}
