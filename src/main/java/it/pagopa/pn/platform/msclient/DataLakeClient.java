package it.pagopa.pn.platform.msclient;

import it.pagopa.pn.platform.datalake.v1.dto.MonthlyNotificationPreorderDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface DataLakeClient {

 Mono<List<MonthlyNotificationPreorderDto>> sendMonthlyOrders ();

}
