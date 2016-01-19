package org.yongzhez.gsonvalidator;


import com.google.gson.*;
import junit.framework.TestCase;

import org.junit.Before;



import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by youngz on 29/12/15.
 */
public class TestValidator extends TestCase{


    protected Validator validator;

    @Before
    public void setUp(){

        this.validator = new Validator();

    }

    /**
     * Taking in a file path, goes through every single one of the tests and runs
     * validator against the file path.
     * @param testJson
     */
    public void testHelper(String testJson){
        try{
            FileReader reader = new FileReader(testJson);
            JsonParser jsonParser = new JsonParser();
            JsonArray root = jsonParser.parse(reader).getAsJsonArray();

            //go through every test/schema set
            for (int i = 0; i < root.size(); i ++){
                JsonObject set = root.get(i).getAsJsonObject();

                JsonObject schema = set.get("schema").getAsJsonObject();
                JsonArray tests = set.get("tests").getAsJsonArray();

                for (JsonElement test: tests){
                    //run validator on every set's test set
                    boolean result = this.validator.validator(test.getAsJsonObject().get("data"),
                            schema);
                    //check against the valid field in the json file, prints out the test case corresponding
                    assertEquals(test.getAsJsonObject().get("description").getAsString(),
                            test.getAsJsonObject().get("valid").getAsBoolean(),
                            result);

                }
            }

        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }catch (IOException ex) {
            ex.printStackTrace();
        }catch (JsonParseException ex) {
            ex.printStackTrace();
        }

    }

    public void testNumeric(){
        Path path = Paths.get("multipleOf.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("maximum.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("minimum.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testString(){
        Path path = Paths.get("maxLength.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("minLength.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testArray(){
        Path path = Paths.get("maxItems.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("minItems.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("items.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("additionalItems.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

//    public void testObject(){
//        Path path = Paths.get("maxProperties.json");
//        this.testHelper(path.toAbsolutePath().toString());
//
//        path = Paths.get("minProperties.json");
//        this.testHelper(path.toAbsolutePath().toString());
//
//        path = Paths.get("required.json");
//        this.testHelper(path.toAbsolutePath().toString());
//
//    }

    public void testRequired(){
        Path path = Paths.get("required.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testMinProperties(){
        Path path = Paths.get("minProperties.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testMaxProperties(){
        Path path = Paths.get("maxProperties.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testProperties(){
        Path path = Paths.get("properties.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testGeneric(){
        Path path = Paths.get("type.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("enum.json");
        this.testHelper(path.toAbsolutePath().toString());
    }
}
