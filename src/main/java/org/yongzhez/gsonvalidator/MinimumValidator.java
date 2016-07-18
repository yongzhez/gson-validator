package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 17/07/16.
 */
public class MinimumValidator {
    /**
     * adheres to section 5.1.3 of Json schema validation for min and exclusive min
     * @param element
     * @param schema
     * @return
     */
    public static boolean validMin(JsonElement element, JsonObject schema){
        Double json = element.getAsDouble();
        Double validate = schema.get("minimum").getAsDouble();
        if (json < validate){
            return false;
        }
        if (schema.has("exclusiveMinimum") && json.equals(validate)){
            return false;
        }
        return true;
    }
}
