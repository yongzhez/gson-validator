package org.yongzhez.gsonvalidator;

import com.google.gson.*;
import org.json.JSONException;

import java.util.Map;


/**
 *gson-validator is a Java library that can be used to validate jsons
 *against json schemas.
 */
public class ObjectValidator implements Validator {

    private PrimitiveValidator primitiveValidator;
    private boolean valid;

    public ObjectValidator() {
        this.primitiveValidator = new PrimitiveValidator();
        this.valid = true;
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
                    valid = ItemsValidator.validItemsArray(properties, schema, this, valid);
                }
                if (schema.get("items").isJsonObject()){
                    valid = ItemsValidator.validItemsObject(properties, schema, this, valid);
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
     * Takes in a jsonElement to be validated and the schema that it validates
     * which adheres to validated generic type jsons
     * @param json
     * @param schema
     * @return
     */
    public boolean validateGeneric(JsonElement json, JsonObject schema){
        boolean valid = true;

        //adheres to section 5.5.2 of Json schema validation for type
        if (schema.has("type")){
            if (schema.get("type").isJsonArray()){
                JsonArray array = schema.get("type").getAsJsonArray();
                //testing for an array of types.
                for (int i = 0; i < array.size(); i ++){
                    String type = array.get(i).getAsJsonPrimitive().getAsString();
                    valid = TypeValidator.typeValidateHelper(type, json);
                    // if even one of the types in the array are met, we return true.
                    // after every iteration check on an element in the type array,
                    // we reset the validation to true for the next iteration
                    if (valid){
                        return true;
                    }
                }
                return false;
            }else{
                valid = TypeValidator.typeValidateHelper(schema.get("type").getAsString(), json);
            }
        }
        //adheres to section 5.5.1 of Json schema validation for enum
        if (schema.has("enum")){
            for (JsonElement enumReq : schema.get("enum").getAsJsonArray()) {
                //first check if the json is an object, if so then evaluate accordingly
                if (enumReq.equals(json)) {
                    valid = true;
                    break ;
                } else {
                    //if enum is one field
                    if (json.isJsonObject() && enumReq.isJsonObject()) {
                        for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject().entrySet()) {
                            for (Map.Entry<String, JsonElement> schemaEntry : enumReq.getAsJsonObject().entrySet()) {
                                if (!entry.getKey().equals(schemaEntry.getKey())) {
                                    valid = false;
                                }
                                if (!entry.getValue().equals(schemaEntry.getValue())) {
                                    valid = false;
                                }
                            }
                        }
                    }
                    else{
                        valid = false;
                    }


                }
            }
        }

        return valid;
    }

    /**
     * Takes in a jsonElement to be validated
     * @param object
     * @param schema
     * @return
     */
    public boolean validateObject(JsonElement object, JsonObject schema){
        JsonObjectValidator JsonObjectValidatore = new JsonObjectValidator();

        if (object.isJsonObject()){
            JsonObject json = object.getAsJsonObject();

            JsonObjectValidatore.validBase(json, schema);

            //adheres to section 5.4.4 of Json schema validation for properties
            if (schema.has("properties")){
                JsonObjectValidatore.validProperties(json, schema, this);
            }
            //adheres to section 5.4.4 of Json schema validation for additionalProperties
        }

        return JsonObjectValidatore.isValid();
    }

    /**
     * takes in a json and a schema and checks if json corresponds to schema
     * @param json a JSONObject
     * @param schema a JSON representation of a schema
     * @return true if json corresponds to schema. false otherwise
     */
    public boolean validator(JsonElement json, JsonObject schema){

        try{
            if (schema.has("required") || schema.has("maxProperties") || schema.has("minProperties")
                    || schema.has("properties")){
                this.valid = this.validateObject(json, schema);
            }
            if (schema.has("type") || schema.has("enum")){
                this.valid = this.validateGeneric(json, schema);
            }

            if (schema.has("additionalItems") || schema.has("items") || schema.has("maxItems") ||
                    schema.has("minItems")){
                this.valid = this.validateArray(json,schema);
            }
            primitiveValidator.setValid(this.valid);
            this.valid = primitiveValidator.validator(json, schema);

            boolean result = this.valid;
            this.valid = true;

            return result;
        }catch (JSONException exception){
            return false;
        }


    }

}
