package com.justinaji.newproj.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.justinaji.newproj.dto.AdminDTO;
import com.justinaji.newproj.dto.UserDTO;
import com.justinaji.newproj.exception.NoUserFound;
import com.justinaji.newproj.model.users;
import com.justinaji.newproj.repo.repouser;

@Service
public class userservice {

    private final repouser urepo;
    public userservice( repouser urepo) {
        this.urepo = urepo;
    }

    public static boolean loggedin = false;
    public static boolean isadmin = false;
    public static String current_user ="";

    // ====== Get Users ======
    public List<?> getuserdets() {
        if (!loggedin) throw new NoUserFound();

        List<users> userslist = urepo.findAll(); // fetch all users from DB

        if (isadmin) {
            List<AdminDTO> adminDTOList = new ArrayList<>();
            for (users u : userslist) {
                AdminDTO dto = new AdminDTO(u.getId(), u.getName(), u.getEmail(), u.isAdmin());
                adminDTOList.add(dto);
            }
            return adminDTOList;
        } else {
            List<UserDTO> userDTOList = new ArrayList<>();
            for (users u : userslist) {
                UserDTO dto = new UserDTO(u.getName(), u.getEmail());
                userDTOList.add(dto);
            }
            return userDTOList;
        }
    }

    // ====== Validate Login ======
    public String validateuser(String email, String pass) {
        List<users> userslist = urepo.findAll(); // fetch all users from DB

        for (users u : userslist) {
            if (u.getEmail().equals(email) && u.getPassword().equals(pass)) {
                loggedin = true;
                if (u.isAdmin()) isadmin = true;
                current_user = u.getId();
                return "user '" + email + "' has successfully logged in";
            }
        }
        return null;
    }

    // ====== Register User ======
    public String userRegister(users user) {
        if (loggedin) return "you must log out to register a new user";

        if (user.getEmail() == null || user.getEmail().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty()) {
            return "Email and password both must be filled";
        }

        if (user.isAdmin() == false) user.setAdmin(false); // Default admin to false if not specified
        String randomId;
        do {
            randomId = CommonMethods.getAlphaNumericString();
        } while (urepo.existsById(randomId)); // ensure uniqueness
        user.setId(randomId);
        urepo.saveAndFlush(user);
        loggedin = true;
        if (user.isAdmin()) isadmin = true;
        current_user = user.getId();

        return "New user '" + user.getEmail() + "' registered successfully";
    }
}
