package org.yongzhez.gsonvalidator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class NumberValidator
{
	/**
	 * adheres to section 5.1.1 of Json schema validation for multiple of
	 * @param element
	 * @param schema
	 * @return
	 */
	public static boolean validMultipleOf(JsonElement element, JsonObject schema){
		//check if either number is below 1, if so, multiply both numbers by
        //10 until both are above 1
		Double json = element.getAsDouble();
        Double validate = schema.get("multipleOf").getAsDouble();
        if ((json < 1 && json > 0)|| (validate < 1 && validate > 0)){
            if (json < validate){
                while (json < 1){
                    json  = json*10;
                    validate = validate*10;
                }
            }
            if (json > validate){
                while (validate < 1){
                    json  = json*10;
                    validate = validate*10;
                }
            }
        }
        if (json % validate != 0){
            return false;
        }
        return true;
	} 
	
	/**
	 * adheres to section 5.1.2 of Json schema validation for max and exclusive max
	 * @param element
	 * @param schema
	 * @return
	 */
	public static boolean validMax(JsonElement element, JsonObject schema){
		Double json = element.getAsDouble();
        Double validate = schema.get("maximum").getAsDouble();
        if (json > validate){
            return false;
        }
        if (schema.has("exclusiveMaximum") && json.equals(validate)){
            return false;
        }
        return true;
	}
	
	/**
	 * adheres to section 5.1.3 of Json schema validation for min and exclusive min
	 * @param element
	 * @param schema
	 * @return
	 */
	public static boolean validMin(JsonElement element, JsonObject schema){
		Double json = element.getAsDouble();
        Double validate = schema.get("minimum").getAsDouble();
        if (json < validate){
            return false;
        }
        if (schema.has("exclusiveMinimum") && json.equals(validate)){
            return false;
        }
        return true;
	}
	
	
}
