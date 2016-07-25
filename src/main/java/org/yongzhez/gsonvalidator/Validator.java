package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 23/07/16.
 */
public interface Validator {

    /**
     * takes in a json and a schema and checks if json corresponds to schema
     * @param json a JSONObject
     * @param schema a JSON representation of a schema
     * @return true if json corresponds to schema. false otherwise
     */
    boolean validator (JsonElement json, JsonObject schema);

}
