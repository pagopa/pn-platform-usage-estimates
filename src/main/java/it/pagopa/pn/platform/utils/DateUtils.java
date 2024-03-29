package it.pagopa.pn.platform.utils;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.ESTIMATE_NOT_EXISTED;


@Slf4j
public class DateUtils {

    private static final ZoneId ZONE_ID = ZoneId.of("UTC");

    private DateUtils(){}

    public static Pair<Instant,Instant> getStartEndFromRefMonth(Instant refMonthInstant){
            Instant start = fromDayMonthYearStart(16, getMonth(minusMonth(refMonthInstant, 2)) , getYear(minusMonth(refMonthInstant, 2)));
            Instant end = fromDayMonthYear(15, getMonth(minusMonth(refMonthInstant, 1)) , getYear(minusMonth(refMonthInstant, 1)));

            return Pair.of(start, end);
    }

    public static Pair<Instant,Instant> getStartEndFromRefYear(Instant refYear){
        Instant start = fromDayMonthYearStart(1, 11 , getYear(minusYear(refYear, 2)));
        Instant end = fromDayMonthYear(31,10 , getYear(minusYear(refYear, 1)));

        return Pair.of(start, end);
    }

    public  static Instant getMaxDeadlineDate (){
        Instant today = Instant.now();
        int addMonth = (getDay(today) > 15) ? 1 : 0;
        Instant max = plusMonth(today, addMonth);
        return fromDayMonthYear(15, getMonth(max), getYear(max));
    }
    public  static Instant getMaxDeadlineYearDate (){
        Instant today = Instant.now();
        int addYear = (getDay(today) == 1 && getMonth(today) == 11) ? 1 : 0;
        Instant max = plusYear(today, addYear);
        return fromDayMonthYear(31, 10, getYear(max));
    }
    public static Instant plusMonth(Instant from, int months) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, ZONE_ID);
        return localDateTime.plusMonths(months).toInstant(ZoneOffset.UTC);
    }
    public static Instant plusYear(Instant from, int year) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, ZONE_ID);
        return localDateTime.plusMonths(year).toInstant(ZoneOffset.UTC);
    }

    public static Instant addOneMonth(Instant from) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, ZONE_ID);
        return localDateTime.plusMonths(1).toInstant(ZoneOffset.UTC);
    }

    public static Instant addOneYear(Instant from) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, ZONE_ID);
        return localDateTime.plusYears(1).toInstant(ZoneOffset.UTC);
    }

    public static Instant minusMonth(Instant from, int months) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, ZONE_ID);
        return localDateTime.minusMonths(months).toInstant(ZoneOffset.UTC);
    }

    public static Instant minusYear(Instant from, int years) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, ZONE_ID);
        return localDateTime.minusYears(years).toInstant(ZoneOffset.UTC);
    }

    public static Instant fromDayMonthYear(int day, int month, int year){
        return LocalDateTime.of(year, month, day, 23, 59).toInstant(ZoneOffset.UTC);
    }

    public static Instant fromDayMonthYearStart(int day, int month, int year){
        return LocalDateTime.of(year, month, day, 00, 00).toInstant(ZoneOffset.UTC);
    }

    public static Integer getYear (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        return localDateTime.getYear();
    }

    public static Integer getMonth (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        return localDateTime.getMonth().getValue();
    }

    public static Integer getDay (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        return localDateTime.getDayOfMonth();
    }

    public static Integer getHour (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return localDateTime.getHour();
    }

    public static Integer getMinute (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        return localDateTime.getMinute();
    }

    public static Integer getSecond (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE_ID);
        return localDateTime.getSecond();
    }

    public static boolean isEqualMonth (Instant uno, Instant due){
        LocalDateTime localDateTimeUno = LocalDateTime.ofInstant(uno, ZONE_ID);
        LocalDateTime localDateTimeDue = LocalDateTime.ofInstant(due, ZONE_ID);
        return localDateTimeUno.getMonth().getValue() == localDateTimeDue.getMonth().getValue()
                && localDateTimeUno.getYear() == localDateTimeDue.getYear();
    }

    public static boolean isEqualYear (Instant uno, Instant due){
        LocalDateTime localDateTimeUno = LocalDateTime.ofInstant(uno, ZONE_ID);
        LocalDateTime localDateTimeDue = LocalDateTime.ofInstant(due, ZONE_ID);
        return localDateTimeUno.getYear() == localDateTimeDue.getYear();
    }

    public static Instant getStartDeadLineDate (){

        Instant now = Instant.now();
        int month = DateUtils.getMonth(now);
        int year = DateUtils.getYear(now);
        if (DateUtils.getDay(now) > 15){
            return DateUtils.addOneMonth(DateUtils.fromDayMonthYear(15, month, year));
        }
        return DateUtils.fromDayMonthYear(15, month, year);
    }

    public static Instant getStartDeadLineDateProfilation (){

        Instant now = Instant.now();
        int month = DateUtils.getMonth(now);
        int year = DateUtils.getYear(now);
        if (DateUtils.getDay(now) == 1 && DateUtils.getMonth(now) == 11){
            return DateUtils.addOneYear(DateUtils.fromDayMonthYear(31, 10, year));
        }
        return DateUtils.fromDayMonthYear(31, 10, year);
    }

    public static Instant toInstant (OffsetDateTime agreementDate){
        if (agreementDate != null) {
            return agreementDate.toInstant();
        } else {
            throw new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage());
        }
    }

    @NotNull
    public static String buildTimestamp(Instant instant) {
        Instant timeStamp = instant.truncatedTo(ChronoUnit.SECONDS);
        return getYear(timeStamp).toString().concat("-")
                .concat(getMonth(timeStamp).toString()).concat("-")
                .concat(getDay(timeStamp).toString()).concat("T")
                .concat(getHour(timeStamp).toString())
                .concat(getMinute(timeStamp).toString())
                .concat(getSecond(timeStamp).toString());
    }

    public static Instant getDateBeforeOneMonth() {
        Instant datebeforeOneMonth = DateUtils.minusMonth(Instant.now(), 1);
        if (Instant.now().isBefore(DateUtils.fromDayMonthYear(15, DateUtils.getMonth(Instant.now()), DateUtils.getYear(Instant.now())))){
            datebeforeOneMonth = DateUtils.minusMonth(Instant.now(), 2);
        }
        return  datebeforeOneMonth;
    }

    public static Instant parseStringTOInstant(String date) {
        return Instant.parse(date);
    }

    public static Long getTimeStampOfMills(LocalDateTime time){
        return time.toInstant(ZoneOffset.UTC).getEpochSecond();
    }

}
