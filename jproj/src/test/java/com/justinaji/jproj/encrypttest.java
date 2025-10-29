package com.justinaji.jproj;

import java.util.Scanner;

import com.justinaji.jproj.service.CommonMethods;

public class encrypttest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter password to encrypt: ");
        String password = sc.nextLine();
        try {
            String encrypted = CommonMethods.encryptpassword(password);
            System.out.println("Encrypted password: " + encrypted);
        } catch (Exception e) {
            System.out.println("Error encrypting password: " + e.getMessage());
        }
    }
}
