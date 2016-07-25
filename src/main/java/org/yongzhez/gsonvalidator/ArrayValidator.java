package org.yongzhez.gsonvalidator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 23/07/16.
 */
public class ArrayValidator extends  BaseValidator{

    private TypeValidator typeValidator;

    public ArrayValidator() {
        this.typeValidator = new TypeValidator();
    }

    /**
     * adheres to section 5.3.1 of Json schema validation for items and additionalItems for Array
     * @param properties
     * @param schema
     * @return
     */
    private void validItemsArray(JsonArray properties, JsonObject schema){

        JsonArray setOfItemType = schema.get("items").getAsJsonArray();
        for (int i = 0; i < properties.size(); i ++){
            if (i >= setOfItemType.size()){
                if ( schema.has("additionalItems")){
                    valid = this.validAdditionalItems(properties, schema, i, valid);
                }else{
                    break;
                }
            }else{
                valid = typeValidator.typeValidation(properties.get(i),
                        setOfItemType.get(i).getAsJsonObject(), valid);
            }
            if (!valid){
                break;
            }
        }

    }

    /**
     * adheres to section 5.3.1 of Json schema validation for items and additionalItems for Object
     * @param properties
     * @param schema
     * @return
     */
    private void validItemsObject(JsonArray properties, JsonObject schema){
        for (JsonElement property: properties){
            //use generic validation for type
            valid = typeValidator.typeValidation(property,
                    schema.get("items").getAsJsonObject(), valid);
            if (!valid){
                break ;
            }
        }


    }

    /**
     *
     * @param properties
     * @param schema
     * @param index
     * @param valid
     * @return
     */
    private boolean validAdditionalItems(JsonArray properties, JsonObject schema, int index, boolean valid){
        if ( schema.get("additionalItems").isJsonPrimitive() &&
                schema.get("additionalItems").getAsJsonPrimitive().isBoolean()){
            if (!schema.get("additionalItems").getAsBoolean()){
                valid = false;
            }
        }else{
            valid = typeValidator.typeValidation(properties.get(index),
                    schema.get("additionalItems").getAsJsonObject(), valid);
        }
        return valid;
    }

    @Override
    protected void validEnum(JsonElement json, JsonObject schema) {
        for (JsonElement enumReq : schema.get("enum").getAsJsonArray()) {
            //first check if the json is an primitive
            if (enumReq.equals(json)) {
                valid =  true;
                return;
            }
        }

        valid = false;
    }

    @Override
    public boolean validator(JsonElement array, JsonObject schema) {

        if (array.isJsonArray()){
            JsonArray properties = array.getAsJsonArray();
            //adheres to section 5.3.1 of Json schema validation for items and additionalItems
            if (schema.has("items")){
                if (schema.get("items").isJsonArray()){
                    this.validItemsArray(properties, schema);
                }
                if (schema.get("items").isJsonObject()){
                    this.validItemsObject(properties, schema);
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
            //TODO
            if (schema.has("type")&& valid){
                valid = typeValidator.typeValidation(array, schema, valid);
            }
            if (schema.has("enum") && valid){
                this.validEnum(array, schema);
            }
            if (schema.has("allOf") && valid){
                this.allOf(array, schema);
            }
            if (schema.has("anyOf") && valid){
                this.anyOf(array, schema);
            }
            if (schema.has("oneOf") && valid){
                this.oneOf(array, schema);
            }
            if (schema.has("not") && valid){
                this.not(array, schema);
            }

        }

        boolean result = this.valid;
        this.valid = true;

        return result;
    }
}
