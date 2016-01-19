package org.yongzhez.gsonvalidator;

import com.google.gson.*;
import org.json.JSONException;
import org.json.JSONString;

import java.util.Map;
import java.util.Set;


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

    /**
     *Takes in a jsonElement to be validated, and the schema that it validates
     * which contains maxLength, minLength, and pattern
     * @param schema a schema containing the above mentioned keywords
     * @param string a jsonElement to be validated ( doesn't have to be string )
     * @return true if Json adheres to schema, false otherwise
     */
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
     *Takes in a jsonElement to be validated, and the schema that it validates
     * which contains items, additionalItems, maxItems, minItems
     * @param schema a schema containing the above mentioned keywords
     * @param array a jsonElement to be validated ( doesn't have to be string )
     * @return true if Json adheres to schema, false otherwise
     */
    public boolean validateArray(JsonElement array, JsonObject schema){
        boolean valid = true;

        if (array.isJsonArray()){
            JsonArray properties = array.getAsJsonArray();
            //adheres to section 5.3.1 of Json schema validation for items and additionalItems
            if (schema.has("items")){
                if (schema.get("items").isJsonArray()){
                    JsonArray setOfItemType = schema.get("items").getAsJsonArray();
                    outerloop:
                    for (int i = 0; i < properties.size(); i ++){
                        if (i >= setOfItemType.size()){
                            if ( schema.has("additionalItems")){
                                if ( schema.get("additionalItems").isJsonPrimitive() &&
                                        schema.get("additionalItems").getAsJsonPrimitive().isBoolean()){
                                    if (!schema.get("additionalItems").getAsBoolean()){
                                        valid = false;
                                    }
                                }else{
                                    valid = this.validateGeneric(properties.get(i),
                                            schema.get("additionalItems").getAsJsonObject());
                                }
                            }else{
                                break outerloop;
                            }
                        }else{
                                valid = this.validateGeneric(properties.get(i),
                                        setOfItemType.get(i).getAsJsonObject());
                        }
                        if (!valid){
                            break outerloop;
                        }
                    }

                }
                if (schema.get("items").isJsonObject()){
                    outerloop:
                    for (JsonElement property: properties){
                        //use generic validation for type
                        valid = this.validateGeneric(property,
                                schema.get("items").getAsJsonObject());
                        if (!valid){
                            break outerloop;
                        }
                    }
                }
            }
            //adheres to section 5.3.2 of Json schema validation for maxItems
            if (schema.has("maxItems")){
                if (properties.size() > schema.get("maxItems").getAsInt()){
                    valid = false;
                }
            }
            //adheres to section 5.3.3 of Json schema validation for minItems
            if (schema.has("minItems")){
                if (properties.size() < schema.get("minItems").getAsInt()){
                    valid = false;
                }
            }
            //adheres to section 5.3.4 of Json schema validation for uniqueItems
        }

        return valid;
    }

    /**
     * helps with type validation for a json element and a string type
     * @param type a string for validation
     * @param json the json to be type checked
     * @param valid the validation to be passed in
     * @return whether the json is the type that it adheres to
     */
    public boolean typeValidateHelper(String type, JsonElement json, boolean valid){

        switch(type){
            case "integer": case "number":
                if (json.isJsonPrimitive()) {
                    JsonPrimitive element = json.getAsJsonPrimitive();
                    if (element.isNumber()) {
                        if (element.getAsNumber().intValue() % element.getAsNumber().doubleValue() != 0 &&
                                type.equals("integer")) {
                            valid = false;
                        }
                    }else{
                        valid = false;
                    }
                }else{
                    valid = false;
                }
                break;
            case "string":
                if (json.isJsonPrimitive()){
                    JsonPrimitive element = json.getAsJsonPrimitive();
                    if (!(element.isString())){
                        valid = false;
                    }
                }else{
                    valid = false;
                }
                break;
            case "object":
                if (!(json.isJsonObject())){
                    valid = false;
                }
                break;
            case "boolean":
                if (json.isJsonPrimitive()){
                    JsonPrimitive element = json.getAsJsonPrimitive();
                    if (!(element.isBoolean())){
                        valid = false;
                    }
                }else{
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

    public boolean validateGeneric(JsonElement json, JsonObject schema){
        boolean valid = true;

        //adheres to section 5.5.2 of Json schema validation for type
        if (schema.has("type")){
            if (schema.get("type").isJsonArray()){
                JsonArray array = schema.get("type").getAsJsonArray();
                //testing for an array of types.
                for (int i = 0; i < array.size(); i ++){
                    String type = array.get(i).getAsJsonPrimitive().getAsString();
                    valid = this.typeValidateHelper(type, json, valid);
                    //if even one of the types in the array are met, we return true
                    //after every iteration check on an element in the type array,
                    // we reset the validation to true for the next iteration
                    if (valid){
                        return true;
                    }else{
                        valid = true;
                    }
                }
                return false;
            }else{
                valid = this.typeValidateHelper(schema.get("type").getAsString(), json, valid);
            }
        }
        //adheres to section 5.5.1 of Json schema validation for enum
        if (schema.has("enum")){
            outerloop:
            for (JsonElement field : schema.get("enum").getAsJsonArray()) {
                //first check if the json is an object, if so then evaluate accordingly
                if (field.equals(json)) {
                    valid = true;
                    break outerloop;
                } else {
                    if (json.isJsonObject() && field.isJsonObject()) {
                        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                            for (Map.Entry<String, JsonElement> schemaEntry : schema.getAsJsonObject().entrySet()) {
                                if (!(entry.getKey().equals(schemaEntry.getKey()) && entry.getValue().equals(schemaEntry.getValue()))) {
                                    valid = false;
                                }
                            }
                        }
                    }
                    valid = false;
                }
            }
        }

        return valid;
    }

    public boolean validateObject(JsonElement object, JsonObject schema){
        boolean valid = true;
        if (object.isJsonObject()){
            //adheres to section 5.4.1 of Json schema validation for maxProperties
            if (schema.has("maxProperties")){
                Integer maxProperty = schema.get("maxProperties").getAsInt();
                Set<Map.Entry<String, JsonElement>>  entrySet = object.getAsJsonObject().entrySet();
                if (entrySet.size() > maxProperty){
                    valid = false;
                }
            }
            //adheres to section 5.4.2 of Json schema validation for minProperties
            if (schema.has("minProperties")){
                Integer maxProperty = schema.get("minProperties").getAsInt();
                Set<Map.Entry<String, JsonElement>>  entrySet = object.getAsJsonObject().entrySet();
                if (entrySet.size() < maxProperty){
                    valid = false;
                }
            }
            //adheres to section 5.4.3 of Json schema validation for required
            if (schema.has("required")){
                JsonArray requiredProperties = schema.get("required").getAsJsonArray();
                for (int i = 0; i < requiredProperties.size(); i ++){
                    if (!(object.getAsJsonObject().has(requiredProperties.get(i).getAsString()))){
                        valid = false;
                    }
                }
            }
            //adheres to section 5.4.4 of Json schema validation for properties
            if (schema.has("properties")){
                JsonObject jsonObject = object.getAsJsonObject();
                outerloop:
                for (Map.Entry<String, JsonElement>  entrySet: schema.get("properties").getAsJsonObject().entrySet()){
                    if (entrySet.getValue().isJsonObject() && jsonObject.has(entrySet.getKey())){
                        valid = this.validator(jsonObject.get(entrySet.getKey()), entrySet.getValue().getAsJsonObject());
                    }
                    if (!valid){
                        break outerloop;

                    }
                }
            }
            //adheres to section 5.4.4 of Json schema validation for additionalProperties
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

        boolean valid = true;

        try{
            if (schema.has("required") || schema.has("maxProperties") || schema.has("minProperties")
                    || schema.has("properties")){
                valid = this.validateObject(json, schema);
            }
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
            if (schema.has("type") || schema.has("enum")){
                valid = this.validateGeneric(json, schema);
            }
            if (schema.has("multipleOf") || schema.has("maximum") || schema.has("exclusiveMaximum") ||
                    schema.has("minimum") || schema.has("exclusiveMinimum")){
                valid = this.validateNumber(json, schema);
            }
            if (schema.has("maxLength") || schema.has("minLength")){
                valid = this.validateString(json, schema);
            }
            if (schema.has("additionalItems") || schema.has("items") || schema.has("maxItems") ||
                    schema.has("minItems")){
                valid = this.validateArray(json,schema);
            }


            return valid;
        }catch (JSONException exception){
            return false;
        }


    }
}
