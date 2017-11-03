package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by niharika on 30-Jun-17.
 */
public class ResponseObj {

    public static JsonNode successObj( JsonNode data, String message) {
        ObjectNode res = Json.newObject();
        res.put("statusCode", 200);
        res.put("data", data);
        res.put("message", message);
        return res;
    }

    public static JsonNode badRequestObj( JsonNode data, String message) {
        ObjectNode res = Json.newObject();
        res.put("statusCode", 400);
        res.put("data", data);
        res.put("message", message);
        return res;
    }

}
