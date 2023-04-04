package it.pagopa.pn.platform.msclient;

import it.pagopa.pn.platform.msclient.generated.pnexternalregistries.v1.dto.PaInfoDto;
import it.pagopa.pn.platform.rest.v1.dto.PAInfo;
import reactor.core.publisher.Mono;


public interface ExternalRegistriesClient  {
    Mono<PaInfoDto> getOnePa(String id);

}
