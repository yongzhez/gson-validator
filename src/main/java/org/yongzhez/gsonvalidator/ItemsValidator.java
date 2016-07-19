package org.yongzhez.gsonvalidator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 17/07/16.
 */
public class ItemsValidator {

    /**
     * adheres to section 5.3.1 of Json schema validation for items and additionalItems for Array
     * @param properties
     * @param schema
     * @param validator
     * @param valid
     * @return
     */
    public static boolean validItemsArray(JsonArray properties, JsonObject schema, Validator validator, boolean valid){
        JsonArray setOfItemType = schema.get("items").getAsJsonArray();
        for (int i = 0; i < properties.size(); i ++){
            if (i >= setOfItemType.size()){
                if ( schema.has("additionalItems")){
                    valid = ItemsValidator.validAdditionalItems(properties, schema, i, validator, valid);
                }else{
                    break;
                }
            }else{
                valid = validator.validateGeneric(properties.get(i),
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
     * @param validator
     * @param valid
     * @return
     */
    public static boolean validItemsObject(JsonArray properties, JsonObject schema, Validator validator, boolean valid){
        for (JsonElement property: properties){
            //use generic validation for type
            valid = validator.validateGeneric(property,
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
     * @param validator
     * @param valid
     * @return
     */
    public static boolean validAdditionalItems( JsonArray properties, JsonObject schema, int index,
                                                Validator validator, boolean valid){
        if ( schema.get("additionalItems").isJsonPrimitive() &&
                schema.get("additionalItems").getAsJsonPrimitive().isBoolean()){
            if (!schema.get("additionalItems").getAsBoolean()){
                valid = false;
            }
        }else{
            valid = validator.validateGeneric(properties.get(index),
                    schema.get("additionalItems").getAsJsonObject());
        }
        return valid;
    }
}
