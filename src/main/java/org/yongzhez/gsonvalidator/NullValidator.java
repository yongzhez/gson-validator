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
        /*instead of writing in the PrimitiveValidator class, it made
        more sense to place this in a class of it's own for readability
        since it has a validation specific to it
         */
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
}
