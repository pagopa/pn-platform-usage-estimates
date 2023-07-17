package it.pagopa.pn.platform.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.csv.CSVPrinter;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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

 /*
 @Override
 public String toString() {
  StringBuilder builder = new StringBuilder();
  builder.append(cap).append(",");
  builder.append(statoEstero).append(",");
  builder.append(numeroPagine).append(",");
  builder.append(pesoPlicoGrammi).append(",");
  builder.append(costo).append(",");
  builder.append(eventId).append(",");
  builder.append(iun).append(",");
  builder.append(notificationSentAt).append(",");
  builder.append(eventTimestamp).append(",");
  builder.append(recipientIndex).append(",");
  builder.append(recipientType).append(",");
  builder.append(recipientTaxId);
  return builder.toString();
 }*/

 public void getPrinter(CSVPrinter csvPrinter) throws IOException {
   csvPrinter.printRecord(
           cap,
           statoEstero,
           numeroPagine,
           pesoPlicoGrammi,
           costo,
           eventId,
           iun,
           notificationSentAt,
           eventTimestamp,
           recipientIndex,
           recipientType,
           recipientTaxId
   );
 }



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
