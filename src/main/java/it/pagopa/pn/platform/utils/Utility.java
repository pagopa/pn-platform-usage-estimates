package it.pagopa.pn.platform.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.UUID;

@Slf4j
public class Utility {

    private Utility(){
        // private constructor
    }

    public static String getPath(String paId, String refMonth, String folder){
        return String.format("paid_%s/month_%s/%s/", paId, refMonth, folder );
    }

    public static String  getSnapshotFileName(String timestamp){
        return String.format("%s_%s_%s.json", Const.MONTHLY, timestamp, UUID.randomUUID());
    }

    public static String  getLastFileName(String refMonth){
        return String.format("%s_%s.json", Const.MONTHLY, refMonth);
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

    public static <T> T jsonToObject(ObjectMapper objectMapper, String json, Class<T> tClass){
        try {

            return objectMapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            log.error("Error with mapping : {}", e.getMessage(), e);
            return null;
        }
    }
}
