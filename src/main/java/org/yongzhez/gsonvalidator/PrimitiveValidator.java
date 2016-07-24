package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Created by youngz on 23/07/16.
 */
public class PrimitiveValidator implements Validator {

    private boolean valid;

    /**
     *Takes in a jsonElement to be validated, and the schema that it validates
     * which contains maxLength, minLength, and pattern
     * @param schema a schema containing the above mentioned keywords
     * @param string a jsonElement to be validated ( doesn't have to be string )
     * @return true if Json adheres to schema, false otherwise
     */
    public boolean validateString(JsonPrimitive string, JsonObject schema){
        boolean valid = true;

        if (string.isString()){
            //adheres to section 5.2.1 of Json schema validation for maxLength
            if (schema.has("maxLength")){
                valid = this.validateLength(string, schema, "maxLength");
            }
            //adheres to section 5.2.2 of Json schema validation for minLength
            if (schema.has("minLength")){
                valid = this.validateLength(string, schema, "minLength");
            }
        }

        return valid;
    }

    /**
     * Takes in a jsonElement to be validated, and the schema that it validates
     * which contains exclusiveMaximum, maximum, multipleOf, minimum or exclusiveMinimum
     * @param schema a schema containing the above mentioned keywords
     * @param number a jsonElement to be validated ( doesn't have to be number )
     * @return true if Json adheres to schema, false otherwise
     */
    public boolean validateNumber(JsonPrimitive number, JsonObject schema){
        boolean valid = true;

        //if jsonElement is a primitve, check if it is a number, if it's a string, ignore it

        if (number.isNumber()){
            //adheres to section 5.1.1 of Json schema validation for multiple of
            if (schema.has("multipleOf")){
                valid = this.validateMultipleOf( number, schema );
            }
            //adheres to section 5.1.2 of Json schema validation for max and exclusive max
            if (schema.has("maximum") || schema.has("exclusiveMaximum")){
                valid = this.validateMax( number, schema );
            }
            //adheres to section 5.1.3 of Json schema validation for min and exclusive min
            if (schema.has("minimum") || schema.has("exclusiveMinimum")){
                valid = this.validateMin( number, schema );
            }
        }
        return valid;
    }

    /**
     * adheres to section 5.1.1 of Json schema validation for multiple of
     * @param element
     * @param schema
     * @return
     */
    private boolean validateMultipleOf(JsonElement element, JsonObject schema){
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

    /**
     * adheres to section 5.2.1 of Json schema validation for maxLength
     * adheres to section 5.2.2 of Json schema validation for minLength
     * @param element
     * @param schema
     * @param type
     * @return
     */
    private boolean validateLength(JsonElement element, JsonObject schema, String type ){
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

    /**
     * adheres to section 5.1.3 of Json schema validation for min and exclusive min
     * @param element
     * @param schema
     * @return
     */
    private boolean validateMin(JsonElement element, JsonObject schema){
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

    /**
     * adheres to section 5.1.2 of Json schema validation for max and exclusive max
     * @param element
     * @param schema
     * @return
     */
    private boolean validateMax(JsonElement element, JsonObject schema){
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

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean validator(JsonElement json, JsonObject schema) {
        if (json.isJsonPrimitive()){
            JsonPrimitive element = json.getAsJsonPrimitive();
            if (schema.has("multipleOf") || schema.has("maximum") || schema.has("exclusiveMaximum") ||
                    schema.has("minimum") || schema.has("exclusiveMinimum")){
                valid = this.validateNumber(element, schema);
            }
            if (schema.has("maxLength") || schema.has("minLength")){
                valid = this.validateString(element, schema);
            }
        }

        return valid;
    }
}
