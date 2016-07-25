package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 24/07/16.
 */
public abstract class BaseValidator implements Validator {

    protected boolean valid;

    protected abstract void validEnum(JsonElement json, JsonObject schema);

    protected void allOf(JsonElement json, JsonObject schema) {
        for (JsonElement req : schema.get("allOf").getAsJsonArray()) {
            if (!this.validator(json, req.getAsJsonObject())){
                this.valid = false;
                return;
            }
        }
    }

    protected void anyOf(JsonElement json, JsonObject schema) {
        for (JsonElement req : schema.get("anyOf").getAsJsonArray()) {
            if (this.validator(json, req.getAsJsonObject())){
                this.valid = true;
                return;
            }
        }
        this.valid = false;

    }

    protected void oneOf(JsonElement json, JsonObject schema) {
        int count = 0;

        for (JsonElement req : schema.get("oneOf").getAsJsonArray()) {
            if (this.validator(json, req.getAsJsonObject())){
                count ++;
            }
            if (count > 1){
                break;
            }
        }
        this.valid = count == 1;
    }

    protected void not(JsonElement json, JsonObject schema) {
        valid = !this.validator(json, schema.get("not").getAsJsonObject());
    }

    protected void validType(JsonElement json, JsonObject schema) {
    }

    protected void setValid(boolean valid) {
        this.valid = valid;
    }
}
