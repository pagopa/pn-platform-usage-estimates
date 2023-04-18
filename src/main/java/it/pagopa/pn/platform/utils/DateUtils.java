package it.pagopa.pn.platform.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.GregorianCalendar;


@Slf4j
public class DateUtils {

    private DateUtils(){}




    public static Instant addOneMonth(Instant from) {
        Instant to = Instant.ofEpochSecond(from.getEpochSecond());
        return to.plus(1, ChronoUnit.MONTHS);
    }

    public static Instant minusMonth(Instant from, int months) {
        Instant to = Instant.ofEpochSecond(from.getEpochSecond());
        return to.minus(months, ChronoUnit.MONTHS);
    }

    public static Instant fromDayMonthYear(int day, int month, int year){
        Date date = new GregorianCalendar(day, month, year).getTime();
        return date.toInstant();
    }



    public static Instant parseStringTOInstant(String date) {
        return Instant.parse(date);
    }



    public static Long getTimeStampOfMills(LocalDateTime time){
        return time.toInstant(ZoneOffset.UTC).getEpochSecond();
    }




}
