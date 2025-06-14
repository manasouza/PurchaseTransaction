package com.wexinc.purchasetransaction.dto;

import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface DefaultMapper {

    @Named("stringToLocalDate")
    default LocalDate stringToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
