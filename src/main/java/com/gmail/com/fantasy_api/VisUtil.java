package com.gmail.com.fantasy_api;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileReader;

@Component
public class VisUtil {


    public JSONObject getOptions(String name) {
        JSONParser parser = new JSONParser(JSONParser.MODE_JSON_SIMPLE);

        Object obj = null;
        try {
            obj = parser.parse(new FileReader(
                    "D:\\programowanie\\fantasy_api\\data\\"+name+".json"));
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return (JSONObject) obj;
    }
}
