package it.pagopa.pn.platform.model;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import it.pagopa.pn.platform.middleware.db.entities.PnProfilation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class TimelineProfilation {

    private PnProfilation actual;

    private List<PnProfilation> history;
}
