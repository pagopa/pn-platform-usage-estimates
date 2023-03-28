package it.pagopa.pn.platform.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class Utility {
    public static String convertToHash(String string) {
        if(string==null){
            return null;
        }
        string = string.toLowerCase().replaceAll("\\s", "");
        return DigestUtils.sha256Hex(string);
    }
}
