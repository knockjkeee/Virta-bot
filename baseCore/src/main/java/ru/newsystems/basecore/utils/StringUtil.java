package ru.newsystems.basecore.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class StringUtil {

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        return str.trim().length() == 0;
    }

    public static String trim(String str) {
        if (isBlank(str)) {
            return "";
        }
        return str.trim();
    }

    public static String serialize(Object o) {
        try {
            return new ObjectMapper().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot serialize " + o, e);
        }
    }

    public static <T> Optional<T> deserialize(String json, Class<T> c) {
        try {
            return Optional.ofNullable(new ObjectMapper().readValue(json, c));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
