package com.suntecgroup.xelerate.cs002manageinvoicebs.function.api;

/* Start of platform import */

import com.google.gson.*;
import com.suntecgroup.xelerate.cs002managecurrencybs.function.function.api.RoundingServiceFunction_1_0;
import com.suntecgroup.xelerate.sunrise.common.service.ManageEntityService;
import com.suntecgroup.xelerate.sunrise.common.utilities.CommonEntityUtils;
import com.suntecgroup.xelerate.sunrise.exception.FunctionalException;
import com.suntecgroup.xelerate.sunrise.logging.XLogger;
import com.suntecgroup.xelerate.sunrise.common.service.ServiceDiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;

import org.junit.jupiter.api.Assertions;
import org.mockito.*;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.mongo.MongoMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.http.HttpMethod;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.function.Predicate;

public class RoundingServiceFunction_1_0Test extends com.suntecgroup.xelerate.sunrise.common.test.PlatformTest {

    
    private Gson gson = new Gson();

    private XLogger xLogger = XLogger.getXLogger(RoundingServiceFunction_1_0Test.class);
    private final String LOGGING_BASE_VALUE = "CS002 : RoundingServiceFunction_1_0Test : JUL-2023 : ";

    
    @BeforeClass
    void setup() {
        gson = new Gson();
        preparePlatformMocks();
        prepareAPIDefaultMocks();

    }
    @InjectMocks
    private RoundingServiceFunction_1_0 api;

    @Test
    public void executeTests() {
    }

    private void preparePlatformMocks() {
        Mockito.when(commonEntityUtils.getVersion(Mockito.anyString(),
                Mockito.any(JsonObject.class),
                Mockito.any(JsonObject.class))).thenReturn("1.0");
    }
    private final Predicate<JsonObject> roundingTemplateRequest = apiRequest ->
            apiRequest.get("beName")
                    .getAsString()
                    .equals("CS002ProductRoundingTemplate");
    private final Predicate<JsonObject> productCurrencyRequest = apiRequest ->
            apiRequest.get("beName")
                    .getAsString()
                    .equals("CS002ProductCurrency");
    private final ArgumentMatcher<JsonObject> roundingTemplateMatcher = apiRequest -> {
        xLogger.debug("{} roundingTemplateMatcher Input {}", LOGGING_BASE_VALUE, apiRequest);
       return Optional.ofNullable(apiRequest)
                .filter(roundingTemplateRequest)
                .isPresent();
    };


    private final ArgumentMatcher<JsonObject> productCurrencyMatcher = apiRequest -> {
        xLogger.debug("{} productCurrencyMatcher Input {}", LOGGING_BASE_VALUE, apiRequest);
        return   Optional.ofNullable(apiRequest)
                .filter(productCurrencyRequest)
                .isPresent();
    };
    Answer<JsonElement> getSummaryOutput = invocationOnMock -> {
        xLogger.debug("{} getDescriptionOutput Input {}", LOGGING_BASE_VALUE, invocationOnMock);
        JsonObject data = invocationOnMock.getArgument(0, JsonObject.class);
        xLogger.debug("{} getDescriptionOutput data {}", LOGGING_BASE_VALUE, data);
        JsonArray op=data.getAsJsonObject("data").getAsJsonArray("functionArray");
        if (op.size()==2 && data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-23T00:00:00.765"))
            return gson.fromJson("[{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"R\",\"function\":\"PRICING\"},{\"precision\":3,\"templateId\":\"25\",\"roundingMethod\":\"C\",\"function\":\"BILLING\"}]"
                    , JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-24T00:00:00.765"))
            return gson.fromJson("[{\"precision\":3,\"templateId\":\"25\",\"roundingMethod\":\"C\",\"function\":\"BILLING\"},{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"F\",\"function\":\"PRICING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-25T00:00:00.765"))
            return gson.fromJson("[{\"precision\":3,\"templateId\":\"25\",\"roundingMethod\":\"C\",\"function\":\"BILLING\"},{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"C\",\"function\":\"PRICING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-26T00:00:00.765"))
            return gson.fromJson("[{\"precision\":3,\"templateId\":\"25\",\"roundingMethod\":\"C\",\"function\":\"BILLING\"},{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"R\",\"function\":\"PRICING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-27T00:00:00.765"))
            return gson.fromJson("[{\"precision\":3,\"templateId\":\"25\",\"roundingMethod\":\"C\",\"function\":\"BILLING\"},{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"F\",\"function\":\"PRICING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-28T00:00:00.765"))
            return gson.fromJson("[{\"precision\":3,\"templateId\":\"25\",\"roundingMethod\":\"C\",\"function\":\"BILLING\"},{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"C\",\"function\":\"PRICING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-29T00:00:00.765"))
            return gson.fromJson("[{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"function\":\"PRICING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-30T00:00:00.765"))
            return gson.fromJson("[{\"precision\":3,\"templateId\":\"25\",\"roundingMethod\":\"C\",\"function\":\"BILLING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-31T00:00:00.765"))
            return gson.fromJson("[]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-21T00:00:00.765"))
            return gson.fromJson("[{\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"function\":\"PRICING\"},{\"precision\":null,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"function\":\"BILLING\"}]",JsonArray.class);
        if (data.getAsJsonObject("data").get("date").getAsString().equals("2023-05-22T00:00:00.765"))
            return gson.fromJson("[{\"precision\":12,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"function\":\"PRICING\"}]",JsonArray.class);

        else
            return new JsonObject();
    };
    Answer<JsonElement> getProductCurrencyOutput = invocationOnMock -> {
        xLogger.debug("{} getDescriptionOutput Input {}", LOGGING_BASE_VALUE, invocationOnMock);
            return gson.fromJson("[{\"denominationFactor\":10,\"roundingPrecision\":6,\"roundingMethod\":\"R\"}]"  , JsonArray.class);

    };

    Answer<JsonElement> getProductCurrencyEmptyOutput = invocationOnMock -> {
        xLogger.debug("{} getDescriptionOutput Input {}", LOGGING_BASE_VALUE, invocationOnMock);
        return gson.fromJson("[]"  , JsonArray.class);

    };
    private void prepareAPIDefaultMocks() {
        Mockito.when(service.executeAPI(Mockito.argThat(productCurrencyMatcher)))
                .thenAnswer(getProductCurrencyOutput);
    }

    @Test
    public void test_ExecuteForValidRoundingAmountWithoutDenomination() {
        String actualInput = "{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"N\",\"date\":\"2023-05-23T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.46591,\"roundingDifference\":\"0.0000011134\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"R\",\"denomination\":0}" ;
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidFlooringAmountWithoutDenomination() {
        String actualInput = "{\"amount\":205.4957165,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"date\":\"2023-05-24T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.49571,\"roundingDifference\":\"0.0000065\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"F\",\"denomination\":0}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        Mockito.when(service.executeAPI(Mockito.argThat(productCurrencyMatcher)))
                .thenAnswer(getProductCurrencyOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidCeilingWithoutDenominationAmount() {
        String actualInput = "{\"amount\":205.4957165,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"date\":\"2023-05-25T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.49572,\"roundingDifference\":\"0.0000035\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"C\",\"denomination\":0}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        Mockito.when(service.executeAPI(Mockito.argThat(productCurrencyMatcher)))
                .thenAnswer(getProductCurrencyOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidRoundingAmountWithDenomination() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-26T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.4660000000,\"roundingDifference\":\"0.0000888866\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"R\",\"denomination\":10}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidFlooringAmountWithDenomination() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-27T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.4659000000,\"roundingDifference\":\"0.0000111134\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"F\",\"denomination\":10}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }

    @Test
    public void test_ExecuteForValidCeilingAmountWithDenomination() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-28T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.4660000000,\"roundingDifference\":\"0.0000888866\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"C\",\"denomination\":10}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidRoundWithTruncate() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-29T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.46,\"roundingDifference\":\"0.0059111134\",\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"denomination\":10}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidRoundWithTruncateLessPrecision() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-22T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.4659111134,\"roundingDifference\":\"0.0000000000\",\"precision\":12,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"denomination\":10}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidRoundWithTruncateNegat() {
        String actualInput ="{\"amount\":-205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-26T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":-205.4660000000,\"roundingDifference\":\"410.9319111134\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"R\",\"denomination\":10}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    //@Test
    public void test_ExecuteForValidRoundWithTruncatehy() {
        String actualInput ="{\"amount\":5.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-26T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":-205.466000000000000000000000000000,\"roundingDifference\":\"410.931911113400000000000000000000\",\"precision\":5,\"templateId\":\"21\",\"roundingMethod\":\"R\",\"denomination\":10}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidRoundingWithNoBillingData() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"N\",\"date\":\"2023-05-29T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.46,\"roundingDifference\":\"0.0059111134\",\"precision\":2,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"denomination\":0}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }

    @Test
    public void test_ExecuteForValidRoundingWithNoFunctionData() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"N\",\"date\":\"2023-05-30T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.466,\"roundingDifference\":\"0.0000888866\",\"precision\":3,\"templateId\":null,\"roundingMethod\":\"R\",\"denomination\":0}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidRoundingWithNoDataInRoundingTemplate() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"N\",\"date\":\"2023-05-31T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.465911,\"roundingDifference\":\"0.0000001134\",\"precision\":6,\"templateId\":null,\"roundingMethod\":\"R\",\"denomination\":0}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForValidRoundingWithNullPrecisionValue() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"N\",\"date\":\"2023-05-21T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.46591111,\"roundingDifference\":\"0.0000000034\",\"precision\":8,\"templateId\":\"21\",\"roundingMethod\":\"T\",\"denomination\":0}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ExecuteForNoRecordInRoundingTemplateAndProductCurrency() {
        String actualInput ="{\"amount\":205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-31T00:00:00.765\"}";
        String expectedOutput ="{\"roundedAmount\":205.4659111134,\"roundingDifference\":0,\"precision\":0,\"templateId\":null,\"roundingMethod\":\"\",\"denomination\":0}";
        Mockito.when(service.executeAPI(Mockito.argThat(roundingTemplateMatcher)))
                .thenAnswer(getSummaryOutput);
                Mockito.when(service.executeAPI(Mockito.argThat(productCurrencyMatcher)))
                        .thenThrow(FunctionalException.class)
                .thenAnswer(getProductCurrencyEmptyOutput);

        JsonObject actualOutput = api.execute(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public void test_ValidDataRetrieve() {
        String actualInput ="{\"data\":{\"amount\":-205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-21T00:00:00.765\"}}";
        String expectedOutput ="{\"amount\":-205.4659111134,\"currencyCode\":\"INR\",\"fx_Rate\":4,\"function\":\"PRICING\",\"applyDenomination\":\"Y\",\"date\":\"2023-05-21T00:00:00.765\"}";

        api.preProcess(gson.fromJson(actualInput, JsonObject.class));

        JsonObject actualOutput = api.dataRetrieve(gson.fromJson(actualInput, JsonObject.class));
        Assertions.assertEquals(expectedOutput, actualOutput.toString());
    }
    @Test
    public final void executeUnWrittenMethodsForCoverage() {
        Assertions.assertDoesNotThrow(() -> api.postProcess(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.methodFlow(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.respond(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.bsValidate(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.dataEnrich(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.enggExtn(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.crossValidationData(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.determineRules(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.decisionTable(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.readBEData(new JsonObject()));
    }

    private final Predicate<JsonObject> roundingRequest = apiRequest ->
            apiRequest.get("beName")
                    .getAsString()
                    .equals("CS002ProductCurrency");



}
