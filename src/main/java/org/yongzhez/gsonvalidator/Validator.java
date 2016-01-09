package org.yongzhez.gsonvalidator;

import com.google.gson.JsonArray;
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
     * @param schema
     * @return
     */
    public boolean validateNumber(JsonElement jsonElement, JsonObject schema){

        boolean valid = true;

        //adheres to section 5.1.1 of Json schema validation for multiple of
        if (schema.has("multipleOf")){
            if (jsonElement.isJsonPrimitive()){
                //if jsonElement is a primitve, check if it is a number, if it's a string, ignore it
                JsonPrimitive element = jsonElement.getAsJsonPrimitive();
                if (element.isNumber()){
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
            }else{
                valid = false;
            }
        }

        if (schema.has("maximum") || schema.has("exclusiveMaximum")){

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
            if (schema.has("multipleOf")){
                valid = this.validateNumber(json, schema);
            }

            return valid;
        }catch (JSONException exception){
            return false;
        }


    }
}