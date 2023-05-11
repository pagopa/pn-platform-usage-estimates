package it.pagopa.pn.platform.model;

import java.util.Arrays;
import java.util.List;

public enum Month {
    GENNAIO("GEN", 1),
    FEBBRAIO("FEB", 2),
    MARZO("MAR",3),
    APRILE("APR",4),
    MAGGIO("MAG",5),
    GIUGNO("GIU",6),
    LUGLIO("LUG",7),
    AGOSTO("AGO",8),
    SETTEMBRE("SET",9),
    OTTOBRE("OTT",10),
    NOVEMBRE("NOV",11),
    DICEMBRE("DIC",12);




    private String value;
    private int number;

    private Month(String value, int number){
        this.value = value;
        this.number = number;
    }

    public static Integer getNumberMonth(String value){
        List<Month> finded = Arrays.stream(Month.values()).filter(month -> month.value.equals(value)).toList();
        if (finded.isEmpty()) return null;
        return finded.get(0).number;
    }

    public static String getValueFromNumber(int number){
        List<Month> finded = Arrays.stream(Month.values()).filter(month -> month.number == (number)).toList();
        if (finded.isEmpty()) return null;
        return finded.get(0).value;
    }

}
