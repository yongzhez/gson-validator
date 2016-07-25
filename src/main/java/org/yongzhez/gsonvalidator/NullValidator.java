package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 24/07/16.
 */
public class NullValidator extends BaseValidator {

    public NullValidator() {
        super();
        this.valid = true;
    }

    @Override
    public boolean validator(JsonElement json, JsonObject schema) {
        if (json.isJsonNull()){
            if (schema.has("enum")){
                this.validEnum(json, schema);
            }
            if (schema.has("type")){
                valid = typeValidator.typeValidation(json, schema, valid);
            }
        }

        boolean result = this.valid;
        this.valid = true;

        return result;

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
}
