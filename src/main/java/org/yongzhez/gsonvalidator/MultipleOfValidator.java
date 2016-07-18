package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 17/07/16.
 */
public class MultipleOfValidator {

    /**
     * adheres to section 5.1.1 of Json schema validation for multiple of
     * @param element
     * @param schema
     * @return
     */
    public static boolean validateNum(JsonElement element, JsonObject schema){
        Double json = element.getAsDouble();
        Double validate = schema.get("multipleOf").getAsDouble();
        if ((json < 1 && json > 0)|| (validate < 1 && validate > 0)){
            if (json < validate){
                while (json < 1){
                    json  = json*10;
                    validate = validate*10;
                }
            }
            if (json > validate){
                while (validate < 1){
                    json  = json*10;
                    validate = validate*10;
                }
            }
        }

        if (json % validate != 0){
            return false;
        }
        return true;
    }
}
