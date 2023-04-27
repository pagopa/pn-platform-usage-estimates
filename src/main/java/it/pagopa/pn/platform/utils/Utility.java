package it.pagopa.pn.platform.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class Utility {
    public static String convertToHash(String string) {
        if(string==null){
            return null;
        }
        string = string.toLowerCase().replaceAll("\\s", "");
        return DigestUtils.sha256Hex(string);
    }
    public static <T> String objectToJson (T data){
        try{
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new Jdk8Module())
                    .registerModule(new JavaTimeModule());
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
            return objectMapper.writeValueAsString(data);
        }
        catch (JsonProcessingException ex){
            log.warn("Error with mapping : {}", ex.getMessage(), ex);
            return null;
        }
    }
}
