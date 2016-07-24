package org.yongzhez.gsonvalidator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 23/07/16.
 */
public class ArrayValidator implements Validator{

    private boolean valid;

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
    public boolean validItemsArray(JsonArray properties, JsonObject schema){
        boolean valid = true;

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
                        setOfItemType.get(i).getAsJsonObject());
            }
            if (!valid){
                break;
            }
        }
        return valid;
    }

    /**
     * adheres to section 5.3.1 of Json schema validation for items and additionalItems for Object
     * @param properties
     * @param schema
     * @return
     */
    public boolean validItemsObject(JsonArray properties, JsonObject schema){
        boolean valid = true;

        for (JsonElement property: properties){
            //use generic validation for type
            valid = typeValidator.typeValidation(property,
                    schema.get("items").getAsJsonObject());
            if (!valid){
                break ;
            }
        }

        return valid;
    }

    /**
     *
     * @param properties
     * @param schema
     * @param index
     * @param valid
     * @return
     */
    public boolean validAdditionalItems(JsonArray properties, JsonObject schema, int index, boolean valid){
        if ( schema.get("additionalItems").isJsonPrimitive() &&
                schema.get("additionalItems").getAsJsonPrimitive().isBoolean()){
            if (!schema.get("additionalItems").getAsBoolean()){
                valid = false;
            }
        }else{
            valid = typeValidator.typeValidation(properties.get(index),
                    schema.get("additionalItems").getAsJsonObject());
        }
        return valid;
    }


    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     *Takes in a jsonElement to be validated, and the schema that it validates
     * which contains items, additionalItems, maxItems, minItems
     * @param schema a schema containing the above mentioned keywords
     * @param array a jsonElement to be validated ( doesn't have to be string )
     * @return true if Json adheres to schema, false otherwise
     */
    @Override
    public boolean validator(JsonElement array, JsonObject schema) {

        if (array.isJsonArray()){
            JsonArray properties = array.getAsJsonArray();
            //adheres to section 5.3.1 of Json schema validation for items and additionalItems
            if (schema.has("items")){
                if (schema.get("items").isJsonArray()){
                    valid = this.validItemsArray(properties, schema);
                }
                if (schema.get("items").isJsonObject()){
                    valid = this.validItemsObject(properties, schema);
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
}
