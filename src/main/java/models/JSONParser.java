package main.java.models;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class JSONParser {

    public static Gson gson = new Gson();

    public static Map<String, String> parse(String body) {
        Type stringStringMap = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> result = gson.fromJson(body, stringStringMap);
        return result;
    }
}
