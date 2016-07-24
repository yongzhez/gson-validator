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
    private ArrayValidator arrayValidator;
    private TypeValidator typeValidator;
    private boolean valid;

    public ObjectValidator() {
        this.primitiveValidator = new PrimitiveValidator();
        this.arrayValidator = new ArrayValidator();
        this.typeValidator = new TypeValidator();
        this.valid = true;
    }


    /**
     * Takes in a jsonElement to be validated and the schema that it validates
     * which adheres to validated generic type jsons
     * @param json
     * @param schema
     * @return
     */
    public boolean validateGeneric(JsonElement json, JsonObject schema){

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
        JsonObjectValidator jsonObjectValidator = new JsonObjectValidator();

        if (object.isJsonObject()){
            JsonObject json = object.getAsJsonObject();

            jsonObjectValidator.validBase(json, schema);

            //adheres to section 5.4.4 of Json schema validation for properties
            if (schema.has("properties")){
                jsonObjectValidator.validProperties(json, schema, this);
            }
            //adheres to section 5.4.4 of Json schema validation for additionalProperties
        }

        return jsonObjectValidator.isValid();
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
            if (schema.has("type")){
                this.valid = typeValidator.typeValidation(json, schema);
            }
            if (schema.has("enum")){
                this.valid = this.validateGeneric(json, schema);
            }

            arrayValidator.setValid(this.valid);
            this.valid = arrayValidator.validator(json, schema);

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
