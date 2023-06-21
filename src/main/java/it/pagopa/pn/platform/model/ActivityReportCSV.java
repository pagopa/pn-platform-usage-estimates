package it.pagopa.pn.platform.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVRecord;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ActivityReportCSV {

 private String cap;
 private String statoEstero;
 private String numeroPagine;
 private String pesoPlicoGrammi;
 private String costo;
 private String eventId;
 private String iun;
 private String notificationSentAt;
 private String eventTimestamp;
 private String recipientIndex;
 private String recipientType;
 private String recipientTaxId;

 public enum Header {
  cap,
  stato_estero,
  iun,
  costo_eurocent,
  event_id,
  event_timestamp,
  numero_pagine,
  notification_sent_at,
  recipient_index,
  peso_plico_g,
  recipient_type,
  recipient_tax_id
 }

}
