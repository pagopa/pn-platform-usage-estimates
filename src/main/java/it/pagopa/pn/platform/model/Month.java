package it.pagopa.pn.platform.model;

import java.util.Arrays;
import java.util.List;

public enum Month {
    GENNAIO("GEN", 1),
    FEBBRAIO("FEBB", 2);


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
        return "MAR";
    }

}
