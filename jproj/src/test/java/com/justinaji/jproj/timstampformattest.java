package com.justinaji.jproj;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class timstampformattest {

    public static void main(String[] args) { 
        List<String> times = new ArrayList<>();
        times.add("2025-11-14T11:08:33.000+05:30");
        times.add("2025-11-18T11:08:33.000+05:30");
        times.add("2026-11-14T11:08:33.000+05:30");

        Date today = new Date(new Timestamp(System.currentTimeMillis()).getTime());
        String formattedDate = new SimpleDateFormat("yyyy/MM/dd").format(today);
        String currentyear = new SimpleDateFormat("yyyy").format(today);

        System.out.println("Timestamp date:\t"+today+"\nFormatted date:\t"+formattedDate+"\nFormatted Year:\t"+currentyear);

        for( String input: times){
            System.out.println("Input: "+input);
            ZonedDateTime zdt = OffsetDateTime.parse(input).atZoneSameInstant(ZoneId.systemDefault());
            //check if gievn date is today or this year 
            String givenyear = zdt.format(DateTimeFormatter.ofPattern("yyyy"));
            String givendate = zdt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            
            String formatted = zdt.format(DateTimeFormatter.ofPattern("dd/MM/yy hh:mm:ss a"));
            String formattedwoutdate = zdt.format(DateTimeFormatter.ofPattern("hh:mm:ss a"));
            String formatteddatentime = zdt.format(DateTimeFormatter.ofPattern("dd/MM hh:mm:ss a"));

            String result;
            if(givenyear.equals(currentyear)){
                result = givendate.equals(formattedDate) ?  formattedwoutdate : formatteddatentime;
            }
            else result = formatted;

            System.out.println(" full timestamp: \t\t"+result+"\n");
        }
    }

    String dateformat(Timestamp timestamp){
        //get current date and year values
        Date date = new Date(new Timestamp(System.currentTimeMillis()).getTime());
        String today = new SimpleDateFormat("yyyy/MM/dd").format(date);
        String currentyear = new SimpleDateFormat("yyyy").format(date);

        //convert given timestamp to it's year and date values
        LocalDateTime ldt = timestamp.toLocalDateTime();
        String givenyear = ldt.format(DateTimeFormatter.ofPattern("yyyy"));
        String givendate = ldt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String result;
        if(givenyear.equals(currentyear)){// compare the two sets
            result = givendate.equals(today) ?  
            ldt.format(DateTimeFormatter.ofPattern("hh:mm:ss a")) : //only time 
            ldt.format(DateTimeFormatter.ofPattern("dd/MM hh:mm:ss a"));  // time with day/month
        }
        else result = ldt.format(DateTimeFormatter.ofPattern("dd/MM/yy hh:mm:ss a")); // full date and time 
        return result;
    }   
}
