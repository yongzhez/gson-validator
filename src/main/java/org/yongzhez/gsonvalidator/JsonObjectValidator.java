package org.yongzhez.gsonvalidator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by youngz on 23/07/16.
 */
public class JsonObjectValidator {

    private boolean valid;
    private List<String> requirements = new ArrayList<>();

    public JsonObjectValidator() {
        this.valid = true;

    }

    public void validBase(JsonObject json, JsonObject schema){
        //adheres to section 5.4.1 of Json schema validation for maxProperties
        if (schema.has("maxProperties")){
            Integer maxProperty = schema.get("maxProperties").getAsInt();
            Set<Map.Entry<String, JsonElement>> entry = json.entrySet();
            if (entry.size() > maxProperty){
                valid = false;
            }
        }
        //adheres to section 5.4.2 of Json schema validation for minProperties
        if (schema.has("minProperties")){
            Integer maxProperty = schema.get("minProperties").getAsInt();
            Set<Map.Entry<String, JsonElement>>  entry = json.entrySet();
            if (entry.size() < maxProperty){
                valid = false;
            }
        }

        if (schema.has("required")){
            JsonArray requiredProperties = schema.get("required").getAsJsonArray();
            for (int i = 0; i < requiredProperties.size(); i ++){
                if (!(json.has(requiredProperties.get(i).getAsString()))){
                    this.valid = false;
                }
                requirements.add(requiredProperties.get(i).getAsString());
            }
        }

    }

    public void validProperties(JsonObject json, JsonObject schema, ObjectValidator objectValidator){
        for (Map.Entry<String, JsonElement>  schemaEntry: schema.get("properties").getAsJsonObject().entrySet()){
            if (schemaEntry.getValue().isJsonObject() && json.has(schemaEntry.getKey())){
                valid = objectValidator.validator(json.get(schemaEntry.getKey()), schemaEntry.getValue().getAsJsonObject());
            }

            if (!valid){
                break;
            }
        }
    }


    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
