package util;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class JsonUtil {

    public static JsonArrayBuilder getJsonArrayBuilder(){
        return Json.createArrayBuilder();
    }

    public static JsonObjectBuilder getJsonObjectBuilder(){
        return Json.createObjectBuilder();
    }
}
