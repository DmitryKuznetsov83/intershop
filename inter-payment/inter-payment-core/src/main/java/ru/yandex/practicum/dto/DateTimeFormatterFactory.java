package ru.yandex.practicum.dto;

import java.time.format.DateTimeFormatter;

public class DateTimeFormatterFactory {

    public static DateTimeFormatter getDefaultDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

}
