package org.yongzhez.gsonvalidator;



import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * Created by youngz on 28/12/15.
 */
public class Validator {

    /**
     * takes in a json and a schema and checks if json corresponds to schema
     * @param json a JSONObject
     * @param schema a JSON representation of a schema
     * @return true if json corresponds to schema. false otherwise
     */
    public boolean validator(JSONObject json, JSONObject schema)
    {
        JSONObject properties = (JSONObject)schema.get("properties");
        boolean valid = false;

        while (properties.keys().hasNext()){
            String key = properties.keys().next();
            JSONObject property = (JSONObject)properties.get(key);

            String type = (String)property.get("type");

            switch (type) {
                case "array":
                    if (json.get(key) instanceof Array){
                        valid = true;
                    }
                case  "boolean":
                    if (json.get(key) instanceof Boolean){
                        valid = true;
                    }
                case "integer":
                    if (json.get(key) instanceof Integer){
                        valid = true;
                    }
                case "number":
                    if (json.get(key) instanceof Float){
                        valid = true;
                    }
                case "null":
                    if (json.get(key) == null){
                        valid = true;
                    }
                case "object":
                    // if there is a nested json, recurse through until return
                    JSONObject nested = (JSONObject)json.get(key);
                    boolean result = this.validator(nested, property);
                case "string":
                    if (json.get(key) instanceof String){
                        valid = true;
                    }
            }
        }

        return valid;
    }
}
