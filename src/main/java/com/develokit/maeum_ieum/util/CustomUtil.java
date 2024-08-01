package com.develokit.maeum_ieum.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class CustomUtil {

    public static LocalDate StringToLocalDate(String birthDate){
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    public static int calculateAge(LocalDate birthDate){
        LocalDate currentDate = LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears();
        } else {
            return 0;
        }
    }
    //파라미터가 현재 시간으로부터 몇 시간 전인지
    public static Long calculateHoursAgo(LocalDateTime lastChatDate){
        if (lastChatDate != null) {
            LocalDateTime currentDateTime = LocalDateTime.now();
            Duration duration = Duration.between(lastChatDate, currentDateTime);
            return duration.toHours();
        } else {
            return null;
        }
    }
}
