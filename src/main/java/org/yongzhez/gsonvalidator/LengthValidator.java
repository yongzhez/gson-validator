package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 17/07/16.
 */
public class LengthValidator {
    /**
     * adheres to section 5.2.1 of Json schema validation for maxLength
     * adheres to section 5.2.2 of Json schema validation for minLength
     * @param element
     * @param schema
     * @param type
     * @return
     */
    public static boolean validLength(JsonElement element, JsonObject schema, String type ){
        String json = element.getAsString();
        Integer validate = schema.get(type).getAsInt();
        if (json.length() > validate && type.equals("maxLength")){
            return false;
        }
        if (json.length() < validate && type.equals("minLength")){
            return false;
        }

        return true;
    }
}
