package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.json.JSONException;



/**
 * Created by youngz on 28/12/15.
 */
public class Validator {



    /**
     * Takes in a jsonElement to be validated, and the schema that it validates
     * which contains exclusiveMaximum, maximum, multipleOf, minimum or exclusiveMinimum
     * @param schema a schema containing the above mentioned keywords
     * @param number a jsonElement to be validated ( doesn't have to be number )
     * @return true if Json adheres to schema, false otherwise
     */
    public boolean validateNumber(JsonElement number, JsonObject schema){

        boolean valid = true;
        //if jsonElement is a primitve, check if it is a number, if it's a string, ignore it
        if (number.isJsonPrimitive()){
            JsonPrimitive element = number.getAsJsonPrimitive();
            if (element.isNumber()){
                //adheres to section 5.1.1 of Json schema validation for multiple of
                if (schema.has("multipleOf")){
                    //check if either number is below 1, if so, multiply both numbers by
                    //10 until both are above 1
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
                        valid = false;
                    }
                }
                //adheres to section 5.1.2 of Json schema validation for max and exclusive max
                if (schema.has("maximum") || schema.has("exclusiveMaximum")){
                    Double json = element.getAsDouble();
                    Double validate = schema.get("maximum").getAsDouble();
                    if (json > validate){
                        valid = false;
                    }
                    if (schema.has("exclusiveMaximum") && json.equals(validate)){
                        valid = false;
                    }
                }
                //adheres to section 5.1.3 of Json schema validation for min and exclusive min
                if (schema.has("minimum") || schema.has("exclusiveMinimum")){
                    Double json = element.getAsDouble();
                    Double validate = schema.get("minimum").getAsDouble();
                    if (json < validate){
                        valid = false;
                    }
                    if (schema.has("exclusiveMinimum") && json.equals(validate)){
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

    public boolean validateString(JsonElement string, JsonObject schema){
        boolean valid = true;

        if (string.isJsonPrimitive()){
            JsonPrimitive element = string.getAsJsonPrimitive();
            if (element.isString()){
                //adheres to section 5.2.1 of Json schema validation for maxLength
                if (schema.has("maxLength")){
                    String json = element.getAsString();
                    Integer validate = schema.get("maxLength").getAsInt();
                    if (json.length() > validate){
                        valid = false;
                    }
                }
                //adheres to section 5.2.2 of Json schema validation for minLength
                if (schema.has("minLength")){
                    String json = element.getAsString();
                    Integer validate = schema.get("minLength").getAsInt();
                    if (json.length() < validate){
                        valid = false;
                    }
                }
            }
        }

        return valid;
    }

    /**
     * takes in a json and a schema and checks if json corresponds to schema
     * @param json a JSONObject
     * @param schema a JSON representation of a schema
     * @return true if json corresponds to schema. false otherwise
     */
    public boolean validator(JsonElement json, JsonObject schema)
    {

        boolean valid = false;

        try{
            //            //get the properties field of a json schema
//            JsonObject properties = schema.get("properties").getAsJsonObject();
//            //iterate through all of the property fields checking for their types
//            for (Map.Entry<String, JsonElement> entry : properties.entrySet()) {
//
//                String key = entry.getKey();
//                JsonElement element = entry.getValue();
//
//                if(element.isJsonObject()){
//                    JsonObject property = element.getAsJsonObject();
//                    if (property.has("type")){
//                        String type = property.get("type").getAsString();
//
//                        switch (type) {
//                            case "object":
//                                // if there is a nested json, recurse through until return
//                                JsonObject nested = (JsonObject)json.get(key);
//                                valid = this.validator(nested, property);
//                            case "string":
//                                if (json.get(key).getAsString() instanceof String){
//                                    valid = true;
//                                }
//                        }
//                    }
//                }
//
//
//
//            }
            if (schema.has("multipleOf") || schema.has("maximum") || schema.has("exclusiveMaximum") ||
                    schema.has("minimum") || schema.has("exclusiveMinimum")){
                valid = this.validateNumber(json, schema);
            }
            if (schema.has("maxLength") || schema.has("minLength")){
                valid = this.validateString(json, schema);
            }


            return valid;
        }catch (JSONException exception){
            return false;
        }


    }
}
