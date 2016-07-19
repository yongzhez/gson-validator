package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by youngz on 18/07/16.
 */
public class PrimitiveTypeValidator {

    /**
     * Validates that a json primitive is the right type
     * @param json
     * @param type
     * @return
     */
    public static boolean validPrimitiveType(JsonElement json,  String type) {
        if (json.isJsonPrimitive()) {
            JsonPrimitive element = json.getAsJsonPrimitive();
            switch(type){
                case "integer": case "number":
                    if (element.isNumber()) {
                        if (element.getAsNumber().intValue() % element.getAsNumber().doubleValue() != 0 &&
                                type.equals("integer")) {
                            return  false;
                        }
                    }else{
                        return false;
                    }
                    break;
                case "string":
                    if (!(element.isString())){
                        return false;
                    }
                    break;
                case "boolean":
                    if (!(element.isBoolean())){
                        return false;
                    }
            }
        }else{
            return false;
        }
        return true;
    }

}
