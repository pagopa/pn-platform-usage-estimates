package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.ProfilationMapper;
import it.pagopa.pn.platform.middleware.db.dao.ProfilationDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.rest.v1.dto.*;

import it.pagopa.pn.platform.service.ProfilationService;
import it.pagopa.pn.platform.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;

import static it.pagopa.pn.platform.exception.ExceptionTypeEnum.*;

@Slf4j
@Service
public class ProfilationServiceImpl implements ProfilationService {

    @Autowired
    private ExternalRegistriesClient externalRegistriesClient;

    @Autowired
    private ProfilationDAO profilationDAO;

    @Override
    public Mono<ProfilationPeriod> createOrUpdateProfilation(String status, String paId, String referenceYear, ProfilationCreateBody profilationCreateBody) {

        Instant refTodayInstant = Instant.now(); //08/06/23
        if (referenceYear == null) {
            return Mono.error(new PnGenericException(REFERENCE_YEAR_NOT_CORRECT, REFERENCE_YEAR_NOT_CORRECT.getMessage()));
        }

        if (Integer.parseInt(referenceYear) < refTodayInstant.get(ChronoField.YEAR)){
            return Mono.error(new PnGenericException(PROFILATION_EXPIRED, PROFILATION_EXPIRED.getMessage()));
        }

        if (Integer.parseInt(referenceYear) > refTodayInstant.get(ChronoField.YEAR)){
            return Mono.error(new PnGenericException(FUTURE_PROFILATION_NOT_EXIST, FUTURE_PROFILATION_NOT_EXIST.getMessage()));
        }

        return this.externalRegistriesClient.getOnePa(paId)
                .flatMap(paInfo -> {
                    Instant onBoardingDate = DateUtils.addOneMonth(DateUtils.toInstant(OffsetDateTime.from(Instant.parse("2022-10-15T10:15:30Z"))));

                    //controllo per vedere se anno che mi viene passato è prima di data di onboarding -> ERRORE
                    if (Integer.parseInt(referenceYear) < onBoardingDate.get(ChronoField.YEAR)) {
                        log.error("ReferenceYear inconsistent with onBoardindate {}", onBoardingDate);
                        return Mono.error(new PnGenericException(PROFILATION_NOT_EXISTED, PROFILATION_NOT_EXISTED.getMessage()));
                    }

                    return this.profilationDAO.getProfilationDetail(paId, referenceYear)
                            //gestire caso in cui profilazione non esiste a db e generarla con TimelineGenerator
                            .switchIfEmpty(Mono.error(new PnGenericException(PROFILATION_NOT_EXISTED, PROFILATION_NOT_EXISTED.getMessage())))
                            .flatMap(pnProfilation -> {
                                if (pnProfilation.getStatus().equals(ProfilationPeriod.StatusEnum.ABSENT.getValue())) {
                                    log.error("PnProfilation inconsistent status. {}", pnProfilation.getStatus());
                                    //mettere mono error
                                    return null;
                                }
                                else if (pnProfilation.getStatus().equalsIgnoreCase(ProfilationPeriod.StatusEnum.VALIDATED.getValue())
                                        && status.equalsIgnoreCase(EstimatePeriod.StatusEnum.DRAFT.getValue())) {
                                    log.error("PnProfilation inconsistent status. {}", pnProfilation.getStatus());
                                    //mettere mono error
                                    return null;
                                }

                                PnProfilation forSave = ProfilationMapper.dtoToPnProfilation(pnProfilation, status, profilationCreateBody);
                                return profilationDAO.createOrUpdate(forSave);
                            })

                            .map(ProfilationMapper::profilationPeriodToDto);
                });
    }

    @Override
    public Mono<ProfilationDetail> getProfilationDetail(String paId, String referenceYear) {
        return null;
    }

    @Override
    public Mono<PageableProfilationResponseDto> getAllProfilations(String paId, String taxId, String ipaId, Integer page, Integer size) {
        return null;
    }

    @Override
    public Mono<ProfilationPeriod> validatedProfilation(String paId, String referenceYear) {
        return null;
    }

}
