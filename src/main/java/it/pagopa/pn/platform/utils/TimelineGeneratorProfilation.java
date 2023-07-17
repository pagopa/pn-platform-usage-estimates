package it.pagopa.pn.platform.utils;


import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.model.TimelineProfilation;
import it.pagopa.pn.platform.rest.v1.dto.ProfilationDetail;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class TimelineGeneratorProfilation {
    private final String paId;

    private final List<PnProfilation> dbList;

    private final List<PnProfilation> timelineList = new ArrayList<>();

    public TimelineGeneratorProfilation(String paId, List<PnProfilation> dbList){
        this.paId = paId;
        this.dbList = new ArrayList<>(dbList);
    }

    public TimelineProfilation extractAllProfilations(Instant onboardingDate) {
        Collections.sort(dbList);
        log.info("Prendo i dati da db: {}", dbList.size());
        Instant currentDeadlineDate = DateUtils.getStartDeadLineDateProfilation();
        log.info("startDeadlineDate: {} ",currentDeadlineDate);
        int lastYear = 0;
        while (currentDeadlineDate.isAfter(onboardingDate)){
            if (!this.dbList.isEmpty() && lastYear < this.dbList.size()
                    && DateUtils.isEqualYear(currentDeadlineDate, this.dbList.get(lastYear).getDeadlineDate())) {
                log.info("Sto per aggiungere profilazione presente a db nello storico: {}", currentDeadlineDate);
                this.timelineList.add(this.dbList.get(lastYear));
                lastYear++;
            } else {
                log.info("Aggiungo profilazione generata per {} deadlineDate", currentDeadlineDate);
                this.timelineList.add(getProfilation(this.paId, null, currentDeadlineDate));
            }

            currentDeadlineDate = DateUtils.minusYear(currentDeadlineDate, 1);
            log.info("Decrementata currentDate di un anno: {}", currentDeadlineDate);
        }
        log.info("Ritorno storico completo {}", timelineList.size());
        return new TimelineProfilation(this.timelineList.get(0), timelineList.subList(1, timelineList.size()));
    }

    /**
     *
     * @param paId
     * @param referenceYear sarà null se chiamato internamente dalla classe TimelineGeneratorProfilation
     * @param deadline può essere null solo se chiamato da Create-GetDetail
     * @return PnProfilation
     */
    public static PnProfilation getProfilation(String paId, String referenceYear, Instant deadline){
        if (referenceYear == null && deadline == null) throw new AssertionError();
        log.info("Creo la prifilazione.");
        PnProfilation profilation = new PnProfilation();
        profilation.setPaId(paId);
        if (deadline == null) {
            log.info("caso in cui non mi è stato passata deadlineDate");
            Instant refYearInstant = DateUtils.fromDayMonthYear(31, 10, Integer.parseInt(referenceYear));
            profilation.setDeadlineDate(DateUtils.minusYear(refYearInstant, 1));
            profilation.setReferenceYear(referenceYear);
        } else if (referenceYear == null) {
            log.info("caso in cui non mi è stato passato referenceYear");
            Instant refYearInstant = DateUtils.addOneYear(deadline);

            String refYear = String.valueOf(DateUtils.getYear(refYearInstant));

            profilation.setDeadlineDate(deadline);
            profilation.setReferenceYear(refYear);
        }


        profilation.setStatus(ProfilationDetail.StatusEnum.DRAFT.getValue());
        if (profilation.getDeadlineDate().isBefore(Instant.now())){
            log.info("profilazione assente");
            profilation.setStatus(ProfilationDetail.StatusEnum.ABSENT.getValue());
        }

        return profilation;
    }

}
