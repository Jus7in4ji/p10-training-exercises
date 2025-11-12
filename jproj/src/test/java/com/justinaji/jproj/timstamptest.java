package com.justinaji.jproj;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

  // 2025-03-07 21:34:46.504
  // Get current java.sql.Timestamp
  

public class timstamptest {
    public static void main(String[] args) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("timestamp : "+timestamp);
        // 2025-03-07 21:34:46.504
        // Get current java.sql.Timestamp from a Date
        Date date = new Date();
        Timestamp timestamp2 = new Timestamp(date.getTime());
        System.out.println("java.sqltimestammp from Date(): "+timestamp2);
        // convert Instant to java.sql.Timestamp
        Timestamp ts = Timestamp.from(Instant.now());
        System.out.println("Convert instant to timestamp: "+ts);        

        // convert ZonedDateTime to Instant to java.sql.Timestamp
        Timestamp ts2 = Timestamp.from(ZonedDateTime.now().toInstant());
        System.out.println("Zonedattime to instant to timestamp: "+ts2);
        // convert java.sql.Timestamp to Instant
        Instant instant = ts.toInstant();
        System.out.println("timestampt to instant: "+instant);
        }

}
