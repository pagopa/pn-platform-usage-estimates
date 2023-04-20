package it.pagopa.pn.platform.utils;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.time.*;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.ESTIMATE_NOT_EXISTED;


@Slf4j
public class DateUtils {

    private static final ZoneId italianZoneId = ZoneId.of("Europe/Rome");

    private DateUtils(){}


    public static Instant addOneMonth(Instant from) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, italianZoneId);
        return localDateTime.plusMonths(1).toInstant(ZoneOffset.UTC);
    }

    public static Instant minusMonth(Instant from, int months) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(from, italianZoneId);
        return localDateTime.minusMonths(months).toInstant(ZoneOffset.UTC);
    }

    public static Instant fromDayMonthYear(int day, int month, int year){

        return LocalDateTime.of(year, month, day, 1, 0).toInstant(ZoneOffset.UTC);

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

    public static Instant parseStringTOInstant(String date) {
        return Instant.parse(date);
    }

    public static Long getTimeStampOfMills(LocalDateTime time){
        return time.toInstant(ZoneOffset.UTC).getEpochSecond();
    }

}
