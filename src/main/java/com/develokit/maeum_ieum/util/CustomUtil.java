package com.develokit.maeum_ieum.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.*;
import java.time.format.DateTimeFormatter;


public class CustomUtil {

    //긴급 알림 내역 화면에 나갈 때 사용
    public static String convertToRelativeTimeString(LocalDateTime localDateTime){
        Duration duration = Duration.between(localDateTime, LocalDateTime.now());
        long days = duration.toDays();

        if(days==0) return localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
        else return days+"일 전";
        //else if(days <= 7) return days+"일 전"; //일주일까지는 'n일 전'으로 반환
        //else return localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

    }

    public static String LocalDateTimeFormatForChatResponse(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM.dd HH:mm");
        return localDateTime.format(formatter);
    }

    //2017-02-03 -> 2017년 2월 3일
    public static String BirthDateToString(LocalDate birthDate){
        String[] split = birthDate.toString().split("-");
        return split[0]+"년 "+split[1]+"월 "+split[2]+"일";
    }

    public static String LocalDateTimeToWeeklyReportPublishedDate(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = localDateTime.format(formatter);
        String[] split = formattedDate.split("-");
        return split[0]+"."+split[1]+"."+split[2]+".";
    }

    public static String LocalDateTimeToMonthlyReportPublishedDate(LocalDateTime localDateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        String formattedDate = localDateTime.format(formatter);
        String[] split = formattedDate.split("-");
        return split[0]+"."+split[1];
    }

    public static String DayOfWeekToString(DayOfWeek dayOfWeek){
        return switch (dayOfWeek){
            case MONDAY -> "(월)";
            case TUESDAY -> "(화)";
            case WEDNESDAY -> "(수)";
            case THURSDAY -> "(목)";
            case FRIDAY -> "(금)";
            case SATURDAY -> "(토)";
            case SUNDAY -> "(일)";
        };
    }

    public static LocalDate StringToLocalDate(String birthDate){
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    public static int calculateAge(LocalDate birthDate){
        LocalDate currentDate = LocalDate.now();
        if ((birthDate != null) && (currentDate != null)) {
            return Period.between(birthDate, currentDate).getYears()+1;
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

    public static String convertToJson(Object object) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        return om.writeValueAsString(object);
    }
}
