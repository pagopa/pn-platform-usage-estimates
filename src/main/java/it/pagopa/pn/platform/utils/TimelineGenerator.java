package it.pagopa.pn.platform.utils;


import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.model.Month;
import it.pagopa.pn.platform.rest.v1.dto.EstimateDto;


import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class TimelineGenerator {

    private String paId;

    //dati estratti dal db
    private List<PnEstimate> dbList ;

    //output
    private List<PnEstimate> timelineList = new ArrayList<>();

    public TimelineGenerator(String paId, List<PnEstimate> dbList){
        this.paId = paId;
        this.dbList = dbList;
    }

    public List<PnEstimate> extractAllEstimates(Instant onboardingDate) {
        //fare get ad external registries per prendere data di onboarding
        //calcolare data di inserimento (data di onboarding + 30gg)
        //calcolare data scadenza
        //fare get all e salvare dati db dentro dbList (mesi)
        //fare controlli per popolare timelineList (controlli tra data inserimento e data scadenza)
        Instant now = Instant.now();
        if (this.dbList == null || this.dbList.isEmpty()) return missingGenerator(now, onboardingDate);

        if (now.get(ChronoField.MONTH_OF_YEAR) > (DateUtils.addOneMonth(this.dbList.get(0).getDeadlineDate()).get(ChronoField.MONTH_OF_YEAR))){
            timelineList.addAll(missingGenerator(now, this.dbList.get(0).getDeadlineDate()));
        }

        int i = 1;
        for (PnEstimate estimateDB : this.dbList) {
            timelineList.add(estimateDB);
            if (this.dbList.size() > (i)){
                timelineList.addAll(missingGenerator(estimateDB.getDeadlineDate(), this.dbList.get(i).getDeadlineDate()));
            } else {
                timelineList.addAll(missingGenerator(estimateDB.getDeadlineDate(), onboardingDate));
            }
            i++;
        }

        return timelineList;
    }

    //metodo per generare mesi missing
    public List<PnEstimate> missingGenerator (Instant start, Instant end){
        List<PnEstimate> tmpEstimates = new ArrayList<>();
        int startMonthReference = DateUtils.addOneMonth(start).get(ChronoField.MONTH_OF_YEAR);
        int lastMonthReference = DateUtils.addOneMonth(end).get(ChronoField.MONTH_OF_YEAR);



        for (int i = 1; i < Math.abs(lastMonthReference-startMonthReference); i++) {
            Instant deadlineDate = DateUtils.minusMonth(start, i);
            tmpEstimates.add(TimelineGenerator.getEstimate(this.paId, null, deadlineDate));
        }


        return tmpEstimates;
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

            String refMonth = Month.getValueFromNumber(refMonthInstant.get(ChronoField.MONTH_OF_YEAR));
            refMonth += "-"+refMonthInstant.get(ChronoField.YEAR);

            estimate.setDeadlineDate(deadline);
            estimate.setReferenceMonth(refMonth);
        }


        estimate.setStatus(EstimateDto.StatusEnum.IN_PROGRESS.getValue());
        if (estimate.getDeadlineDate().isBefore(Instant.now())){
            estimate.setStatus(EstimateDto.StatusEnum.ENDED.getValue());
        }

        return estimate;
    }


}
