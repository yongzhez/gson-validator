package org.yongzhez.gsonvalidator;

import com.google.gson.*;
import org.json.JSONException;

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
                    valid = MultipleOfValidator.validateNum( element, schema );
                }
                //adheres to section 5.1.2 of Json schema validation for max and exclusive max
                if (schema.has("maximum") || schema.has("exclusiveMaximum")){
                    valid = MaximumValidator.validMax( element, schema );
                }
                //adheres to section 5.1.3 of Json schema validation for min and exclusive min
                if (schema.has("minimum") || schema.has("exclusiveMinimum")){
                    valid = MinimumValidator.validMin( element, schema );
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
                    valid = LengthValidator.validLength(element, schema, "maxLength");
                }
                //adheres to section 5.2.2 of Json schema validation for minLength
                if (schema.has("minLength")){
                    valid = LengthValidator.validLength(element, schema, "minLength");
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
            for (JsonElement field : schema.get("enum").getAsJsonArray()) {
                //first check if the json is an object, if so then evaluate accordingly
                if (field.equals(json)) {
                    valid = true;
                    break ;
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
                for (Map.Entry<String, JsonElement>  entrySet: schema.get("properties").getAsJsonObject().entrySet()){
                    if (entrySet.getValue().isJsonObject() && jsonObject.has(entrySet.getKey())){
                        valid = this.validator(jsonObject.get(entrySet.getKey()), entrySet.getValue().getAsJsonObject());
                    }
                    if (!valid){
                        break;

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
