package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 17/07/16.
 */
public class MaximumValidator {
    /**
     * adheres to section 5.1.2 of Json schema validation for max and exclusive max
     * @param element
     * @param schema
     * @return
     */
    public static boolean validMax(JsonElement element, JsonObject schema){
        Double json = element.getAsDouble();
        Double validate = schema.get("maximum").getAsDouble();
        if (json > validate){
            return false;
        }
        if (schema.has("exclusiveMaximum") && json.equals(validate)){
            return false;
        }
        return true;
    }
}
