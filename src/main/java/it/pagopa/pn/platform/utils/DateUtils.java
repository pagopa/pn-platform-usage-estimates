package it.pagopa.pn.platform.utils;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.util.Pair;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.ESTIMATE_NOT_EXISTED;


@Slf4j
public class DateUtils {

    private static final ZoneId italianZoneId = ZoneId.of("Europe/Rome");

    private DateUtils(){}

    public static Pair<Instant,Instant> getStartEndFromRefMonth(Instant refMonthInstant){
            Instant start = fromDayMonthYear(16, getMonth(minusMonth(refMonthInstant, 2)) , getYear(minusMonth(refMonthInstant, 2)));
            Instant end = fromDayMonthYear(15, getMonth(minusMonth(refMonthInstant, 1)) , getYear(minusMonth(refMonthInstant, 1)));

            return Pair.of(start, end);
    }

    public  static Instant getMaxDeadlineDate (){
        Instant today = Instant.now();
        int addMonth = (getDay(today) > 15) ? 1 : 0;
        Instant max = plusMonth(today, addMonth);
        return fromDayMonthYear(15, getMonth(max), getYear(max));
    }
    public static Instant plusMonth(Instant from, int months) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, italianZoneId);
        return localDateTime.plusMonths(months).toInstant(ZoneOffset.UTC);
    }

    public static Instant addOneMonth(Instant from) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, italianZoneId);
        return localDateTime.plusMonths(1).toInstant(ZoneOffset.UTC);
    }

    public static Instant minusMonth(Instant from, int months) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, italianZoneId);
        return localDateTime.minusMonths(months).toInstant(ZoneOffset.UTC);
    }

    public static Instant fromDayMonthYear(int day, int month, int year){
        return LocalDateTime.of(year, month, day, 22, 0).toInstant(ZoneOffset.UTC);
    }

    public static Integer getYear (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, italianZoneId);
        return localDateTime.getYear();
    }

    public static Integer getMonth (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, italianZoneId);
        return localDateTime.getMonth().getValue();
    }

    public static Integer getDay (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, italianZoneId);
        return localDateTime.getDayOfMonth();
    }

    public static Integer getHour (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        return localDateTime.getHour();
    }

    public static Integer getMinute (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, italianZoneId);
        return localDateTime.getMinute();
    }

    public static Integer getSecond (Instant instant){
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, italianZoneId);
        return localDateTime.getSecond();
    }

    public static boolean isEqualMonth (Instant uno, Instant due){
        LocalDateTime localDateTimeUno = LocalDateTime.ofInstant(uno, italianZoneId);
        LocalDateTime localDateTimeDue = LocalDateTime.ofInstant(due, italianZoneId);
        return localDateTimeUno.getMonth().getValue() == localDateTimeDue.getMonth().getValue()
                && localDateTimeUno.getYear() == localDateTimeDue.getYear();
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

    public static Instant toInstant (OffsetDateTime agreementDate){
        if (agreementDate != null) {
            return agreementDate.toInstant();
        }else {
            throw new PnGenericException(ESTIMATE_NOT_EXISTED, ESTIMATE_NOT_EXISTED.getMessage());
        }
    }

    @NotNull
    public static String buildTimestamp(PnEstimate pnEstimate) {
        Instant timeStamp = pnEstimate.getLastModifiedDate().truncatedTo(ChronoUnit.SECONDS);
        return getYear(timeStamp).toString().concat("-")
                .concat(getMonth(timeStamp).toString()).concat("-")
                .concat(getDay(timeStamp).toString()).concat("T")
                .concat(getHour(timeStamp).toString())
                .concat(getMinute(timeStamp).toString())
                .concat(getSecond(timeStamp).toString());
    }

    public static Instant parseStringTOInstant(String date) {
        return Instant.parse(date);
    }

    public static Long getTimeStampOfMills(LocalDateTime time){
        return time.toInstant(ZoneOffset.UTC).getEpochSecond();
    }

}
