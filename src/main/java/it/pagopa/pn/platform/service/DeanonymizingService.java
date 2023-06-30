package it.pagopa.pn.platform.service;

import reactor.core.publisher.Mono;


public interface DeanonymizingService {

    Mono<Void> execute (String paId, String fileKey);
}
