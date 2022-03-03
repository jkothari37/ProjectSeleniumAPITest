import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class APITests {

    String baseUrl = "https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest";

    @BeforeTest
    @Description("Set Pre required steps")
    public void setUp(){
        RestAssured.baseURI = this.baseUrl;
        RestAssured.urlEncodingEnabled = false;
    }

    @Test(priority = 0, description = "Verify the number of Items")
    @Description("Test Get The Token")
    public void testItemsAreGreaterThan(){
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.headers("Content-Type", ContentType.ANY, "Accept", ContentType.ANY);
        Response response = requestSpecification.get("/currencies.json");
        JsonPath js = new JsonPath(response.asString());
        Assert.assertEquals(response.getStatusCode(),200);
        System.out.println("List of Items Size : " +js.getInt("size()"));
        Assert.assertTrue(js.getInt("size()") > 12);
    }

    @Test(priority = 1, description = "Verify the number of Items")
    @Description("Test United States in Response Json")
    public void testUnitedStatesDollarInResponse(){
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.headers("Content-Type", ContentType.ANY, "Accept", ContentType.ANY);
        Response response = requestSpecification.get("/currencies.json");
        Assert.assertEquals(response.getStatusCode(),200);
        System.out.println("USD : " +JsonPath.from(response.asString()).get("usd"));
        Assert.assertNotNull(JsonPath.from(response.asString()).get("usd"));
    }

    @Test(priority = 2, description = "Verify the number of Items")
    @Description("Test British Pound in Response Json")
    public void testBritishPoundInResponse(){
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.headers("Content-Type", ContentType.ANY, "Accept", ContentType.ANY);
        Response response = requestSpecification.get("/currencies.json");
        Assert.assertEquals(response.getStatusCode(),200);
        System.out.println("GBP : " +JsonPath.from(response.asString()).get("gbp"));
        Assert.assertNotNull(JsonPath.from(response.asString()).get("gbp"));
    }

    @Test(priority = 3, description = "Save Abbreviations")
    @Description("Save All Abbreviations")
    public void testSaveAllAbbreviations() throws Exception {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.headers("Content-Type", ContentType.ANY, "Accept", ContentType.ANY);
        Response response = requestSpecification.get("/currencies.json");
        Assert.assertEquals(response.getStatusCode(), 200);

        List<String> abbreviations = getAllAbbreviations(response);
        System.out.println("List of Items count : " + abbreviations.size());
        Assert.assertNotNull(abbreviations);
    }


    @Test(priority = 4, description = "Currency List All Countries")
    @Description("Currency List All Countries")
    public void testCurrencyListAllCountries() throws Exception {
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.headers("Content-Type", ContentType.ANY, "Accept", ContentType.ANY);
        Response response = requestSpecification.get("/currencies.json");
        Assert.assertEquals(response.getStatusCode(), 200);
        Map<String, Map<String,Double>> currencyPair = new HashMap<>();
        for (String currency : getAllAbbreviations(response)){
            Response res = requestSpecification.get("/currencies/" + currency + ".json");
            Map<String, Double> parsedJsonObject = JsonPath.from(res.asString()).get(currency);

            if(null!=response.getBody()) {
                currencyPair.put(currency,parsedJsonObject);
            }
        }
        System.out.println(("Currency Pairs : " + currencyPair));
    }

    private  List<String> getAllAbbreviations(Response response) throws Exception{
        Map<String, String> parsedJsonObject = new ObjectMapper().readValue(response.getBody().asString(),
                new TypeReference<Map<String, String>>() {
                });

        return new ArrayList<>(parsedJsonObject.keySet());
    }

}



