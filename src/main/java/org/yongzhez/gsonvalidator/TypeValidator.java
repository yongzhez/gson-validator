package org.yongzhez.gsonvalidator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by youngz on 18/07/16.
 */
public class TypeValidator {

    public boolean typeValidation(JsonElement json, JsonObject schema, boolean valid){

        //adheres to section 5.5.2 of Json schema validation for type
        if (schema.has("type")){
            if (schema.get("type").isJsonArray()){
                JsonArray array = schema.get("type").getAsJsonArray();
                //testing for an array of types.
                for (int i = 0; i < array.size(); i ++){
                    String type = array.get(i).getAsJsonPrimitive().getAsString();
                    valid = this.typeValidateHelper(type, json);
                    // if even one of the types in the array are met, we return true.
                    // after every iteration check on an element in the type array,
                    // we reset the validation to true for the next iteration
                    if (valid){
                        return true;
                    }
                }
                return false;
            }else{
                valid = this.typeValidateHelper(schema.get("type").getAsString(), json);
            }
        }
        return valid;
    }

    /**
     * helps with type validation for a json element and a string type
     * @param type a string for validation
     * @param json the json to be type checked
     * @return whether the json is the type that it adheres to
     */
    private boolean typeValidateHelper(String type, JsonElement json){
        boolean valid = true;

        switch(type){
            case "integer": case "number": case "string":  case "boolean":
                valid = this.validPrimitiveType(type, json);
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
    private boolean validPrimitiveType( String type, JsonElement json) {
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
