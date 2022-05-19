package util;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

public class JsonUtil{

    public static JsonArrayBuilder getJsonArrayBuilder() {
        return Json.createArrayBuilder();
    }

    public static JsonObjectBuilder getJsonObjectBuilder() {
        return Json.createObjectBuilder();
    }

   /* public static JsonObjectBuilder generateResponse(int status, String message, String data) {
        responseInfo = Json.createObjectBuilder();
        if (status == 200) {
            responseInfo.add("status", status);
            responseInfo.add("message", message);
            responseInfo.add("data", data);

        } else if (status == 400) {
            responseInfo.add("status", status);
            responseInfo.add("message", message);
            responseInfo.add("data", data);

        } else {
            responseInfo.add("status", status);
            responseInfo.add("message", message);
            responseInfo.add("data", data);
        }
        return responseInfo;
    }*/

    public static void generateResponse(int status, String message, Object data) {
        JsonObjectBuilder responseInfo = Json.createObjectBuilder();
        if (data instanceof JsonArray) {
            System.out.println(status+" "+message+" "+ (JsonArray)data);
            return;
        }
        System.out.println(status+" "+message+" "+ (String)data);
        /*if (status == 200) {
            responseInfo.add("status", status);
            responseInfo.add("message", message);
            responseInfo.add("data", data);


        } else if (status == 400) {
            responseInfo.add("status", status);
            responseInfo.add("message", message);
            responseInfo.add("data", data);

        } else {
            responseInfo.add("status", status);
            responseInfo.add("message", message);
            responseInfo.add("data", data);
        }*/
//        return responseInfo;
    }
}
