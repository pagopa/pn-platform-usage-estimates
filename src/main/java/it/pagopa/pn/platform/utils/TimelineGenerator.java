package it.pagopa.pn.platform.utils;


import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.model.TimelineEstimate;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDetail;


import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimelineGenerator {

    private String paId;

    private List<PnEstimate> dbList;

    private List<PnEstimate> timelineList = new ArrayList<>();

    public TimelineGenerator(String paId, List<PnEstimate> dbList){
        this.paId = paId;
        this.dbList = new ArrayList<>(dbList);
    }

    public TimelineEstimate extractAllEstimates(Instant onboardingDate, String paId) {
        Collections.sort(dbList);
        Instant currentDate = DateUtils.getStartDeadLineDate();
        int lastMonth = 0;
        while (currentDate.isAfter(onboardingDate)){
                if (this.dbList != null && !this.dbList.isEmpty() && lastMonth < this.dbList.size()
                        && DateUtils.isEqualMonth(currentDate, this.dbList.get(lastMonth).getDeadlineDate())) {
                    this.timelineList.add(this.dbList.get(lastMonth));
                    lastMonth++;
                } else {
                    this.timelineList.add(getEstimate(this.paId, null, currentDate));
                }
            currentDate = DateUtils.minusMonth(currentDate, 1);
        }
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

        PnEstimate estimate = new PnEstimate();
        estimate.setPaId(paId);
        estimate.setLastModifiedTimestamp(Instant.now());
        if (deadline == null) {
            String[] splitMonth = referenceMonth.split("-");
            int numberOfMonth = Month.getNumberMonth(splitMonth[0]);
            Instant refMonthInstant = DateUtils.fromDayMonthYear(15, numberOfMonth, Integer.parseInt(splitMonth[1]));
            estimate.setDeadlineDate(DateUtils.minusMonth(refMonthInstant, 1));
            estimate.setReferenceMonth(referenceMonth);
        } else if (referenceMonth == null) {
            Instant refMonthInstant = DateUtils.addOneMonth(deadline);

            String refMonth = Month.getValueFromNumber(DateUtils.getMonth(refMonthInstant));
            refMonth += "-"+DateUtils.getYear(refMonthInstant);

            estimate.setDeadlineDate(deadline);
            estimate.setReferenceMonth(refMonth);
        }


        estimate.setStatus(EstimateDetail.StatusEnum.DRAFT.getValue());
        if (estimate.getDeadlineDate().isBefore(Instant.now())){
            estimate.setStatus(EstimateDetail.StatusEnum.ABSENT.getValue());
        }

        return estimate;
    }



}
