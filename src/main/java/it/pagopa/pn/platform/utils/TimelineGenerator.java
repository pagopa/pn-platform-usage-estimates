package it.pagopa.pn.platform.utils;


import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.model.TimelineEstimate;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;
import lombok.extern.slf4j.Slf4j;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j

public class TimelineGenerator {

    private final String paId;

    private final List<PnEstimate> dbList;

    private final List<PnEstimate> timelineList = new ArrayList<>();

    public TimelineGenerator(String paId, List<PnEstimate> dbList){
        this.paId = paId;
        this.dbList = new ArrayList<>(dbList);
    }

    public TimelineEstimate extractAllEstimates(Instant onboardingDate) {
        Collections.sort(dbList);
        log.info("Prendo i dati da db: {}", dbList.size());
        Instant currentDate = DateUtils.getStartDeadLineDate();
        log.info("startDeadlineDate: {} ",currentDate);
        int lastMonth = 0;
        while (currentDate.isAfter(onboardingDate)){
                if (!this.dbList.isEmpty() && lastMonth < this.dbList.size() && DateUtils.isEqualMonth(currentDate, this.dbList.get(lastMonth).getDeadlineDate())) {
                    log.info("Sto per aggiungere stima presente a db nello storico: {}", currentDate);
                    this.timelineList.add(this.dbList.get(lastMonth));
                    lastMonth++;
                } else {
                    log.info("Aggiungo stima generata per {} deadlineDate", currentDate);
                    this.timelineList.add(getEstimate(this.paId, null, currentDate));
                }

            currentDate = DateUtils.minusMonth(currentDate, 1);
            log.info("Decrementata currentDate di un mese: {}", currentDate);
        }
        log.info("Ritorno storico completo {}", timelineList.size());
        return new TimelineEstimate(this.timelineList.get(0), timelineList.subList(1, timelineList.size()));
    }

    /**
     *
     * @param paId
     * @param referenceMonth sarà null se chiamato internamente dalla classe TimelineGenerator
     * @param deadline può essere null solo se chiamato da Create-GetDetail
     * @return PnEstimate
     */
    public static PnEstimate getEstimate(String paId, String referenceMonth, Instant deadline){
        if (referenceMonth == null && deadline == null) throw new AssertionError();
        log.info("Creo la stima.");
        PnEstimate estimate = new PnEstimate();
        estimate.setPaId(paId);
        if (deadline == null) {
            log.info("caso in cui non mi è stato passata deadlineDate");
            String[] splitMonth = referenceMonth.split("-");
            int numberOfMonth = Month.getNumberMonth(splitMonth[0]);
            Instant refMonthInstant = DateUtils.fromDayMonthYear(15, numberOfMonth, Integer.parseInt(splitMonth[1]));
            estimate.setDeadlineDate(DateUtils.minusMonth(refMonthInstant, 1));
            estimate.setReferenceMonth(referenceMonth);
        } else if (referenceMonth == null) {
            log.info("caso in cui non mi è stato passato referenceMonth");
            Instant refMonthInstant = DateUtils.addOneMonth(deadline);

            String refMonth = Month.getValueFromNumber(DateUtils.getMonth(refMonthInstant));
            refMonth += "-"+DateUtils.getYear(refMonthInstant);

            estimate.setDeadlineDate(deadline);
            estimate.setReferenceMonth(refMonth);
        }


        estimate.setStatus(EstimateDetail.StatusEnum.DRAFT.getValue());
        if (estimate.getDeadlineDate().isBefore(Instant.now())){
            log.info("stima assente");
            estimate.setStatus(EstimateDetail.StatusEnum.ABSENT.getValue());
        }

        return estimate;
    }



}
