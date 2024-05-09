package com.delmark.portfoilo.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
    public static String getDateString(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        return formatter.format(date);
    }

    //Этот метод использует Instant для конвертации LocalDate в Date.
    //Сначала LocalDate преобразуется в ZonedDateTime с помощью atStartOfDay() и atZone(ZoneId.systemDefault()), что добавляет время и часовой пояс к LocalDate.
    //Затем ZonedDateTime преобразуется в Instant с помощью toInstant().
    //Наконец, Instant преобразуется в Date с помощью Date.from(Instant).
    public static Date convertToDateViaInstant(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    //Этот метод использует java.sql.Date в качестве промежуточного шага для конвертации LocalDate в Date.
    //Сначала LocalDate преобразуется в long с помощью toEpochDay(), который возвращает количество дней, прошедших с эпохи Unix (1970-01-01).
    //Затем это значение умножается на количество миллисекунд в одном дне (24 * 60 * 60 * 1000), чтобы получить количество миллисекунд с эпохи Unix.
    public static Date convertToDateViaSqlDate(LocalDate localDate) {
        return new Date(localDate.toEpochDay() * 24 * 60 * 60 * 1000);
    }
}
