package com.justinaji.jproj;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class timstampformattest {

    public static void main(String[] args) { 
        String input = "2025-11-14T11:08:33.000+05:30";

ZonedDateTime zdt = OffsetDateTime.parse(input).atZoneSameInstant(ZoneId.systemDefault());
String formatted = zdt.format(DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss"));

System.out.println(formatted);

    }
}
