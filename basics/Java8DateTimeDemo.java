import java.time.*;

public class Java8DateTimeDemo {
    public static void main(String[] args) {
        System.out.println("-- a few java 8 datetime api methods-- \n");

        // LocalDate 
        LocalDate today = LocalDate.now();
        LocalDate specificDate = LocalDate.of(2025, Month.OCTOBER, 27);
        LocalDate parsedDate = LocalDate.parse("2023-05-20");

        System.out.println("LocalDate Examples:\nToday: " + today);
        System.out.println("Specific Date: " + specificDate);
        System.out.println("Parsed Date: " + parsedDate);
        System.out.println("Tomorrow: " + today.plusDays(1));
        System.out.println("Last Week: " + today.minusWeeks(1));
        System.out.println("Is Leap Year? " + today.isLeapYear());

        // LocalTime 
        LocalTime now = LocalTime.now();
        LocalTime specificTime = LocalTime.of(14, 30, 45);
        LocalTime parsedTime = LocalTime.parse("10:15:30");

        System.out.println("\nLocalTime Examples:\nCurrent Time: " + now);
        System.out.println("Specific Time: " + specificTime);
        System.out.println("Parsed Time: " + parsedTime);
        System.out.println("Two Hours Later: " + now.plusHours(2));
        System.out.println("15 Minutes Ago: " + now.minusMinutes(15));

        // LocalDateTime 
        LocalDateTime dateTimeNow = LocalDateTime.now();
        LocalDateTime customDateTime = LocalDateTime.of(2025, Month.DECEMBER, 25, 10, 30);

        System.out.println("\nLocalDateTime Examples:\nCurrent DateTime: " + dateTimeNow);
        System.out.println("Custom DateTime: " + customDateTime);
        System.out.println("One Week Later: " + dateTimeNow.plusWeeks(1));

        // ZonedDateTime 
        ZonedDateTime zonedNow = ZonedDateTime.now();
        ZonedDateTime indiantime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
        ZonedDateTime specificZoneTime = ZonedDateTime.of(customDateTime, ZoneId.of("Asia/Tokyo"));

        System.out.println("\nZonedDateTime Examples:\nSystem Default Zone: " + zonedNow);
        System.out.println("Indian time : " + indiantime);
        System.out.println("Tokyo time: " + specificZoneTime);

        // ZoneIds
        System.out.println("\nAvailable Zone IDs (sample 5):");
        ZoneId.getAvailableZoneIds().stream().limit(5).forEach(System.out::println);
    }
}
