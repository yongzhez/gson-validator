package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by youngz on 18/07/16.
 */
public class TypeValidator {

    /**
     * helps with type validation for a json element and a string type
     * @param type a string for validation
     * @param json the json to be type checked
     * @return whether the json is the type that it adheres to
     */
    public static boolean typeValidateHelper(String type, JsonElement json){
        boolean valid = true;

        switch(type){
            case "integer": case "number": case "string":  case "boolean":
                valid = TypeValidator.validPrimitiveType(type, json);
                break;
            case "object":
                if (!(json.isJsonObject())){
                    valid = false;
                }
                break;
            case "array":
                if (!(json.isJsonArray())){
                    valid = false;
                }
                break;
            case "null":
                if (!(json.isJsonNull())){
                    valid = false;
                }
                break;
        }
        return valid;
    }

    /**
     * Validates that a json primitive is the right type
     * @param json
     * @param type
     * @return
     */
    public static boolean validPrimitiveType( String type, JsonElement json) {
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
