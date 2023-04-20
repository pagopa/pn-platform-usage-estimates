package it.pagopa.pn.platform.model;

import it.pagopa.pn.platform.middleware.db.entities.PnEstimate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class TimelineEstimate {

    private PnEstimate actual;

    private List<PnEstimate> history;


}
