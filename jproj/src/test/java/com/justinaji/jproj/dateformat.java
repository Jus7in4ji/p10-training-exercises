package com.justinaji.jproj;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

public class dateformat {

    public static void main(String[] args) {

        Timestamp currTS = new Timestamp(System.currentTimeMillis());
        System.out.println("The Current Timestamp == " + currTS);
        System.out.println("The Type of Original DateTime == " + currTS.getClass().getName());
        
        Date currDate = new Date(currTS.getTime());
        DateFormat formattedDate = new SimpleDateFormat("yyyy/MM/dd");
        String date = formattedDate.format(currDate);
        
        System.out.println("The Formatted Date == " + date);
        System.out.println("The Type of Converted DateTime == " + currDate.getClass().getName());
        }
}