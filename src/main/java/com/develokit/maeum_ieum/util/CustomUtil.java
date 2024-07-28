package com.develokit.maeum_ieum.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomUtil {

    public static LocalDate StringToLocalDate(String birthDate){
        return LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
