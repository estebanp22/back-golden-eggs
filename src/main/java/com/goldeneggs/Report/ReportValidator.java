package com.goldeneggs.Report;


import java.time.LocalDate;
import java.util.List;

public class ReportValidator {

    private static final List<String> VALID_TYPES = List.of("INCIDENTE", "VENTAS", "DIARIO", "MANTENIMIENTO");

    public static boolean validateType(String type) {return type != null && VALID_TYPES.contains(type.toUpperCase());}

    public static boolean validateDate(LocalDate dateReport) {return dateReport != null;}

    public static boolean validateContent(String content) {return content != null && content.length() >= 10;}

}
