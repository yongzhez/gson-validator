package org.yongzhez.gsonvalidator;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import junit.framework.TestCase;

import org.junit.Before;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

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
                    assertEquals(set.get("description").getAsString()
                            + "/" +  test.getAsJsonObject().get("description").getAsString(),
                            test.getAsJsonObject().get("valid").getAsBoolean(),
                            result);

                }
            }

        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
            fail();
        }catch (JsonParseException ex) {
            ex.printStackTrace();
            fail();
        }

    }

    public void test_multipleOf_KeyWord(){
        Path path = Paths.get("TestCases/multipleOf.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void test_maximum_Keyword(){
        Path path = Paths.get("TestCases/maximum.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void test_minimum_Keyword(){
        Path path = Paths.get("TestCases/minimum.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void test_MinAndMaxLength_Keyword(){
        Path path = Paths.get("TestCases/maxLength.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("TestCases/minLength.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void test_items_KeyWord(){
        Path path = Paths.get("TestCases/items.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("TestCases/additionalItems.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void test_type_KeyWord(){
        Path path = Paths.get("TestCases/type.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testArray(){
        Path path = Paths.get("TestCases/maxItems.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("TestCases/minItems.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testObject(){
        Path path = Paths.get("TestCases/maxProperties.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("TestCases/minProperties.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("TestCases/required.json");
        this.testHelper(path.toAbsolutePath().toString());

    }

    public void testRequired(){
        Path path = Paths.get("TestCases/required.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testMinProperties(){
        Path path = Paths.get("TestCases/minProperties.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testMaxProperties(){
        Path path = Paths.get("TestCases/maxProperties.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testProperties(){
        Path path = Paths.get("TestCases/properties.json");
        this.testHelper(path.toAbsolutePath().toString());
    }

    public void testGeneric(){
        Path path = Paths.get("TestCases/type.json");
        this.testHelper(path.toAbsolutePath().toString());

        path = Paths.get("TestCases/enum.json");
        this.testHelper(path.toAbsolutePath().toString());
    }
}
