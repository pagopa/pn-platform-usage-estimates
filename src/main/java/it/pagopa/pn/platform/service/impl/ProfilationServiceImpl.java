package it.pagopa.pn.platform.service.impl;

import it.pagopa.pn.platform.exception.PnGenericException;
import it.pagopa.pn.platform.mapper.ProfilationMapper;
import it.pagopa.pn.platform.middleware.db.dao.ProfilationDAO;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import it.pagopa.pn.platform.msclient.ExternalRegistriesClient;
import it.pagopa.pn.platform.rest.v1.dto.*;

import it.pagopa.pn.platform.service.ProfilationService;
import it.pagopa.pn.platform.utils.DateUtils;
import it.pagopa.pn.platform.utils.TimelineGeneratorProfilation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;

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

        Instant refTodayInstant = Instant.now();
        if (referenceYear == null) {
            return Mono.error(new PnGenericException(REFERENCE_YEAR_NOT_CORRECT, REFERENCE_YEAR_NOT_CORRECT.getMessage()));
        }

        if (Integer.parseInt(referenceYear) < DateUtils.getYear(refTodayInstant)){
            return Mono.error(new PnGenericException(PROFILATION_EXPIRED, PROFILATION_EXPIRED.getMessage()));
        }

        if (Integer.parseInt(referenceYear) >= DateUtils.getYear(refTodayInstant) + 2){
            return Mono.error(new PnGenericException(FUTURE_PROFILATION_NOT_EXIST, FUTURE_PROFILATION_NOT_EXIST.getMessage()));
        }

        return this.externalRegistriesClient.getOnePa(paId)
                .flatMap(paInfo -> {
                    Instant onBoardingDate = DateUtils.addOneMonth(DateUtils.toInstant(paInfo.getAgreementDate()));

                    //controllo per vedere se anno che mi viene passato è prima di data di onboarding -> ERRORE
                    if (Integer.parseInt(referenceYear) < DateUtils.getYear(onBoardingDate)) {
                        log.error("ReferenceYear inconsistent with onBoardindate {}", onBoardingDate);
                        return Mono.error(new PnGenericException(PROFILATION_NOT_EXISTED, PROFILATION_NOT_EXISTED.getMessage()));
                    }

                    return this.profilationDAO.getProfilationDetail(paId, referenceYear)
                            .switchIfEmpty(Mono.just(TimelineGeneratorProfilation.getProfilation(paId,referenceYear, null )))
                            .flatMap(pnProfilation -> {
                                if (pnProfilation.getStatus().equals(ProfilationPeriod.StatusEnum.ABSENT.getValue())) {
                                    log.error("PnProfilation inconsistent status. {}", pnProfilation.getStatus());
                                    return Mono.error(new PnGenericException(PROFILATION_NOT_EXISTED, PROFILATION_NOT_EXISTED.getMessage()));
                                } else if (pnProfilation.getStatus().equalsIgnoreCase(ProfilationPeriod.StatusEnum.VALIDATED.getValue())
                                        && status.equalsIgnoreCase(ProfilationPeriod.StatusEnum.DRAFT.getValue())) {
                                    log.error("PnProfilation inconsistent status. {}", pnProfilation.getStatus());
                                    return Mono.error(new PnGenericException(OPERATION_NOT_ALLOWED, OPERATION_NOT_ALLOWED.getMessage()));
                                }
                                PnProfilation forSave = ProfilationMapper.dtoToPnProfilation(pnProfilation, status, profilationCreateBody);
                                return profilationDAO.createOrUpdate(forSave);
                            })
                            .map(ProfilationMapper::profilationPeriodToDto);
                });
    }

    @Override
    public Mono<ProfilationDetail> getProfilationDetail(String paId, String referenceYear) {
        Instant refYearInstant = Instant.now();

        if (referenceYear == null) {
            return Mono.error(new PnGenericException(REFERENCE_YEAR_NOT_CORRECT, REFERENCE_YEAR_NOT_CORRECT.getMessage()));
        }

        Instant deadlineRefYear = DateUtils.minusYear(refYearInstant,1);
        Instant maxDeadlineDate = DateUtils.getMaxDeadlineYearDate();


        if (maxDeadlineDate.isBefore(deadlineRefYear))
            return Mono.error(new PnGenericException(PROFILATION_NOT_EXISTED, PROFILATION_NOT_EXISTED.getMessage(), HttpStatus.NOT_FOUND));

        return this.externalRegistriesClient.getOnePa(paId)
                .zipWhen(paInfo -> {
                    Instant onBoardingDate = DateUtils.addOneMonth(DateUtils.toInstant(paInfo.getAgreementDate()));
                    if (refYearInstant.isBefore(onBoardingDate)){
                        log.error("ReferenceYear inconsistent with onBoardindate {}", onBoardingDate);
                        return Mono.error(new PnGenericException(PROFILATION_NOT_EXISTED, PROFILATION_NOT_EXISTED.getMessage(), HttpStatus.NOT_FOUND));
                    }
                    log.debug("Retrieve profilation from db and create it if it's not present.");
                    return this.profilationDAO.getProfilationDetail(paId,referenceYear)
                            .switchIfEmpty(Mono.just(TimelineGeneratorProfilation.getProfilation(paId, referenceYear, null)));
                })
                .map(paInfoAndProfilation -> ProfilationMapper.profilationDetailToDto(paInfoAndProfilation.getT2(), paInfoAndProfilation.getT1()));

    }

    @Override
    public Mono<PageableProfilationResponseDto> getAllProfilations(String paId, String taxId, String ipaId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return this.externalRegistriesClient.getOnePa(paId)
                .flatMap(paInfoDto ->
                        this.profilationDAO.getAllProfilations(paId)
                                .map(pnProfilations -> {
                                            log.debug("Build timeline.");
                                            TimelineGeneratorProfilation timelineGeneratorProfilation = new TimelineGeneratorProfilation(paId, pnProfilations);
                                            return timelineGeneratorProfilation.extractAllProfilations(DateUtils.toInstant(paInfoDto.getAgreementDate()));
                                        }
                                )
                )
                .map(list -> ProfilationMapper.toPageableResponse(pageable, list));
    }



    @Override
    public Mono<ProfilationPeriod> validatedProfilation(String paId, String referenceYear) {
        Instant refYearInstant = Instant.now();
        if (referenceYear == null) {
            return Mono.error(new PnGenericException(REFERENCE_YEAR_NOT_CORRECT, REFERENCE_YEAR_NOT_CORRECT.getMessage()));
        }

        if (Integer.parseInt(referenceYear) < DateUtils.getYear(refYearInstant)){
            return Mono.error(new PnGenericException(PROFILATION_EXPIRED, PROFILATION_EXPIRED.getMessage()));
        }

        if (Integer.parseInt(referenceYear) > DateUtils.getYear(refYearInstant)+1){
            return Mono.error(new PnGenericException(FUTURE_PROFILATION_NOT_EXIST, FUTURE_PROFILATION_NOT_EXIST.getMessage()));
        }

        return this.profilationDAO.getProfilationDetail(paId, referenceYear)
                .switchIfEmpty(Mono.error(new PnGenericException(PROFILATION_NOT_EXISTED, PROFILATION_NOT_EXISTED.getMessage())))
                .flatMap(pnProfilation -> {
                    if (pnProfilation.getStatus().equalsIgnoreCase(ProfilationPeriod.StatusEnum.DRAFT.getValue())
                            && pnProfilation.getDeadlineDate().isAfter(refYearInstant)){
                        pnProfilation.setStatus(ProfilationPeriod.StatusEnum.VALIDATED.getValue());
                        pnProfilation.setLastModifiedDate(refYearInstant);
                        return profilationDAO.createOrUpdate(pnProfilation);
                    }
                    return Mono.just(pnProfilation);
                }).map(ProfilationMapper::profilationPeriodToDto);
    }

}
