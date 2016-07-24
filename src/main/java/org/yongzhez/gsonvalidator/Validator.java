package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by youngz on 23/07/16.
 */
public interface Validator {

    boolean validator (JsonElement json, JsonObject schema);
}
