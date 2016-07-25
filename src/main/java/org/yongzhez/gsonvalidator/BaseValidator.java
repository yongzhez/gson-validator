package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 24/07/16.
 */
public abstract class BaseValidator implements Validator {

    protected boolean valid;

    @Override
    public void allOf(JsonElement json, JsonObject schema) {
        for (JsonElement req : schema.get("allOf").getAsJsonArray()) {
            if (!this.validator(json, req.getAsJsonObject())){
                this.valid = false;
                return;
            }
        }
    }

    @Override
    public void anyOf(JsonElement json, JsonObject schema) {
        for (JsonElement req : schema.get("anyOf").getAsJsonArray()) {
            if (this.validator(json, req.getAsJsonObject())){
                this.valid = true;
                return;
            }
        }
        this.valid = false;

    }

    @Override
    public void oneOf(JsonElement json, JsonObject schema) {
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

    @Override
    public void not(JsonElement json, JsonObject schema) {
        valid = !this.validator(json, schema.get("not").getAsJsonObject());
    }

    @Override
    public void validType(JsonElement json, JsonObject schema) {
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
