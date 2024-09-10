package com.develokit.maeum_ieum.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class CustomUtil {

    public static String LocalDateTimeFormatForChatResponse(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm");
        return localDateTime.format(formatter);
    }

    //2017-02-03 -> 2017년 2월 3일
    public static String BirthDateToString(LocalDate birthDate){
        String[] split = birthDate.toString().split("-");
        return split[0]+"년 "+split[1]+"월 "+split[2]+"일";
    }

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
    public static LocalDateTime[] getWeekStartAndEnd(LocalDateTime dateTime){
        LocalDate date = dateTime.toLocalDate();
        LocalDate startOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return new LocalDateTime[]{
                LocalDateTime.of(startOfWeek, LocalDateTime.MIN.toLocalTime()),
                LocalDateTime.of(endOfWeek, LocalDateTime.MAX.toLocalTime())
        };
    }
    public static LocalDateTime getMonthEnd(LocalDateTime dateTime){ //해당 달이 끝나는 날 반환
        YearMonth yearMonth = YearMonth.from(dateTime);
        LocalDate lastDayOfMonth = yearMonth.atEndOfMonth();

        return LocalDateTime.of(lastDayOfMonth, LocalTime.MAX);
    }

    public static String convertToJson(Object object) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.writeValueAsString(object);
    }
}
