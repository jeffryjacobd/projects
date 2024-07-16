package com.suntecgroup.xelerate.cs002manageinvoicebs.function.api;

/* Start of platform import */

import com.suntecgroup.xelerate.cs001manageinputoutputinterfacebs.function.function.api.AuthenticateChannelFunction_1_0;
import com.suntecgroup.xelerate.sunrise.common.service.ManageEntityService;
import com.suntecgroup.xelerate.sunrise.common.utilities.CommonEntityUtils;
import com.suntecgroup.xelerate.sunrise.exception.FunctionalException;
import com.suntecgroup.xelerate.sunrise.exception.InvalidateRecordException;
import com.suntecgroup.xelerate.sunrise.logging.XLogger;
import com.suntecgroup.xelerate.sunrise.common.service.ServiceDiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonNull;

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

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.BeforeEach;

import org.mockito.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.ArrayList;
/* End of platform import */

@SpringBootTest
@EnableAutoConfiguration(exclude = {MongoMetricsAutoConfiguration.class})
public class AuthenticateChannelFunction_1_0Test extends AbstractTestNGSpringContextTests {

    private XLogger xLogger = XLogger.getXLogger(AuthenticateChannelFunction_1_0Test.class);
    private final String LOGGING_BASE_VALUE = "CS001 : AuthenticateChannelFunction_1_0Test : MAY-2023 : ";
    @Autowired
    private Gson gson;

    @SpringBootConfiguration
    @ComponentScan(basePackages = {"com.suntecgroup.xelerate.sunrise.bs.common", "com.suntecgroup.xelerate.sunrise.common",
            "com.suntecgroup.xelerate.cs001manageinputoutputinterfacebs.*"})
    public static class TestApplication {
        
        public GsonBuilder gsonBuilder() {
            return new GsonBuilder();
        }
    }
    @Autowired
    @Qualifier("cS001ManageInputOutputInterfaceBS/authenticateChannel_1.0")
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private AuthenticateChannelFunction_1_0 api;
    @Mock
    CommonEntityUtils commonEntityUtils;
    @Mock
    ManageEntityService service;
    /*
     * This test Method will test the "dataRetrieve" method
     * and Assert with expected output and actualOutput
     */
    @Test(dataProvider = "prepare_InputAndExpectedOutput_for_dataRetrieve")
    public void dataRetrieveMethodTest(String input, String output){
        xLogger.debug("Starting the execution of test_dataRetrieve");
        JsonObject actualInput = new Gson().fromJson(input, JsonObject.class);
        JsonObject expectedOutput = new Gson().fromJson(output, JsonObject.class);
        JsonObject actualOutput = new JsonObject();
        try {
            api.preProcess(actualInput);
            actualOutput = api.dataRetrieve(actualInput);
            xLogger.debug("{} ActualOutput: {}", LOGGING_BASE_VALUE, actualOutput.toString());
            xLogger.debug("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput.toString());
            JSONAssert.assertEquals(expectedOutput.toString(), actualOutput.toString(), JSONCompareMode.LENIENT);
            xLogger.debug("{} passed test_dataRetrieve", LOGGING_BASE_VALUE);
        } catch (JSONException e) {
            xLogger.error("{} Failed to parse results to JSON: {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} Failed test_execute", LOGGING_BASE_VALUE);
        } catch (AssertionError e) {
            xLogger.error("{} Assertion Error {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput);
            xLogger.error("{} ActualResult: {}", LOGGING_BASE_VALUE, actualOutput);
            xLogger.error("{} Failed test_dataRetrieve {}", LOGGING_BASE_VALUE);
            throw e;
        }
    }
    /*
     * This Method will give the input and expected output for dataRetrieve method
     */

    @DataProvider(name = "prepare_InputAndExpectedOutput_for_dataRetrieve")
    private Object[][] prepare_InputAndExpectedOutput_for_dataRetrieve() {
        xLogger.debug("Starting prepare_InputAndExpectedOutput_for_dataRetrieve");
        return new Object[][]{
                new Object[]{
                        "{\"data\":{\"interfaceCode\":\"TRAN001\"},\"version\":\"1\"}",
                        "{\"interfaceCode\":\"TRAN001\"}"
                }
        };
    }

    /*
     *This test Method will test the "readBEData" method
     * and Assert with expected output and actualOutput
     */

    @Test(dataProvider = "prepare_InputAndExpectedOutput_for_readBEData")
    public void readBEDataMethodTest(String input, String output){
        xLogger.debug("Starting the execution of readBEDataMethodTest");
        JsonObject actualInput = new Gson().fromJson(input, JsonObject.class);
        JsonObject expectedOutput = new Gson().fromJson(output, JsonObject.class);
        JsonObject actualOutput = new JsonObject();
        try {
            Mockito.when(commonEntityUtils.getVersion(Mockito.any(String.class), Mockito.any(JsonObject.class), Mockito.any(JsonObject.class)))
                    .thenReturn("1.0");
            api.preProcess(actualInput);
            actualOutput = api.readBEData(actualInput);
            xLogger.debug("{} ActualOutput: {}", LOGGING_BASE_VALUE, actualOutput.toString());
            xLogger.debug("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput.toString());
            JSONAssert.assertEquals(expectedOutput.toString(), actualOutput.toString(), JSONCompareMode.LENIENT);
            xLogger.debug("{} passed test_readBEData", LOGGING_BASE_VALUE);
        } catch (JSONException e) {
            xLogger.error("{} Failed to parse results to JSON: {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} Failed test_readBEData", LOGGING_BASE_VALUE);
        } catch (AssertionError e) {
            xLogger.error("{} Assertion Error {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput);
            xLogger.error("{} ActualResult: {}", LOGGING_BASE_VALUE, actualOutput);
            xLogger.error("{} Failed test_readBEData {}", LOGGING_BASE_VALUE);
            throw e;
        }
    }
    /*
     * This Method will give the input and expected output for readBEData method
     */

    @DataProvider(name = "prepare_InputAndExpectedOutput_for_readBEData")
    private Object[][] prepare_InputAndExpectedOutput_for_readBEData() {
        xLogger.debug("Starting prepare_InputAndExpectedOutput_for_readBEData");
        return new Object[][]{
                new Object[]{
                        "{\"interfaceCode\":\"16\"}",
                        "{\"beName\":\"CS001ProductInputOutputInterface\",\"mode\":\"read\",\"version\":\"1.0\",\"data\":{\"template\":\"authenticateChannel\",\"interfaceCode\":\"16\"}}"
                }
        };
    }
    /*
     *This test Method will test the "execute" method
     * and Assert with expected output and actualOutput
     */

    @Test(dataProvider = "prepare_InputAndMockAndExpectedOutput_for_execute")
    public void executeMethodTestValidInterfaceCode(String input, String output, List<JsonElement> executeMock){
        xLogger.debug("Starting the execution of executeMethodTestValidInterfaceCode");
        JsonObject actualInput = new Gson().fromJson(input, JsonObject.class);
        JsonObject expectedOutput = new Gson().fromJson(output, JsonObject.class);
        JsonObject actualOutput = new JsonObject();
        try {
            Mockito.when(commonEntityUtils.getVersion(Mockito.any(String.class), Mockito.any(JsonObject.class),
                    Mockito.any(JsonObject.class))).thenReturn("1.0");
            Mockito.when(service.executeAPI(Mockito.any(JsonObject.class))).
                    thenAnswer(AdditionalAnswers.returnsElementsOf(executeMock));
            api.preProcess(actualInput);
            actualOutput = api.execute(actualInput);
            xLogger.debug("{} ActualOutput: {}", LOGGING_BASE_VALUE, actualOutput.toString());
            xLogger.debug("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput.toString());
            JSONAssert.assertEquals(expectedOutput.toString(), actualOutput.toString(), JSONCompareMode.LENIENT);
            xLogger.debug("{} passed test_execute", LOGGING_BASE_VALUE);
        } catch (JSONException e) {
            xLogger.error("{} Failed to parse results to JSON: {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} Failed test_execute", LOGGING_BASE_VALUE);
        } catch (AssertionError e) {
            xLogger.error("{} Assertion Error {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput);
            xLogger.error("{} ActualResult: {}", LOGGING_BASE_VALUE, actualOutput);
            xLogger.error("{} Failed test_execute {}", LOGGING_BASE_VALUE);
            throw e;
        }
    }
    /*
     * This Method will give the input and expected output for execute method
     */

    @DataProvider(name = "prepare_InputAndMockAndExpectedOutput_for_execute")
    private Object[][] prepare_InputAndMockAndExpectedOutput_for_execute() {
        xLogger.debug("Starting prepare_InputAndMockAndExpectedOutput_for_execute");
        return new Object[][]{
                new Object[]{
                        "{\"beName\":\"CS001ProductInputOutputInterface\",\"mode\":\"read\",\"version\":\"1.0\",\"data\":{\"template\":\"authenticateChannel\",\"interfaceCode\":\"16\"}}",
                        "{\"authenticateChannel\":[{\"authenticationAndAuthorizationSourceSytem\":{\"username\":\"admin\",\"password\":\"admin123\"}}]}                                                                                                                                                                                                     ",
                        prepareServiceExecuteApiForResponseBEUsingInterfaceCode()
                }

        };
    }
    private List<JsonElement> prepareServiceExecuteApiForResponseBEUsingInterfaceCode() {
        xLogger.debug("{} starting prepareServiceExecuteApiForExecute", LOGGING_BASE_VALUE);
        String mock1 = "[{\"authenticationAndAuthorizationSourceSytem\":{\"username\":\"admin\",\"password\":\"admin123\"}}]";
        List<JsonElement> returnJsonObjectList = new LinkedList<>();
        Stream.of(mock1).forEach(mock -> returnJsonObjectList.add(new Gson().fromJson(mock, JsonElement.class)));
        xLogger.debug("{} return output {}", LOGGING_BASE_VALUE, returnJsonObjectList);
        return returnJsonObjectList;
    }

    /*
     * This test Method will test the "execute" method by giving input
     * and Assert with expected output and actualOutput
     */
    @Test (dataProvider = "prepare_InputAndExpectedOutput_for_ExecuteForFailure",expectedExceptions = FunctionalException.class)
    public void test_ExecuteFailure(String input) {
        xLogger.debug("{} Starting the execution of test_ExecuteFailure", LOGGING_BASE_VALUE);
        JsonObject actualInput = new Gson().fromJson(input, JsonObject.class);
        Mockito.when(service.executeAPI(Mockito.any(JsonObject.class)))
                .thenThrow(InvalidateRecordException.class);
        api.preProcess(new JsonObject());
        api.execute(actualInput);
    }

    /*
     * This Method will give the input and expected output and mockOutput of manageEntity api for execute method
     */
    @DataProvider(name = "prepare_InputAndExpectedOutput_for_ExecuteForFailure")
    private Object[][] prepare_InputAndExpectedOutput_for_ExecuteForFailure() {
        xLogger.debug("Starting prepare_InputAndMockAndExpectedOutput_for_ReadBE");
        return new Object[][]{
                new Object[]{
                        "{\"beName\":\"CS001ProductInputOutputInterface\",\"mode\":\"read\",\"version\":\"1.0\",\"data\":{\"template\":\"authenticateChannel\",\"interfaceCode\":\"160\"}}"
                }
        };
    }
    /*
     * This test Method will test the "respond" method by giving input
     * and Assert with expected output and actualOutput
     */

    @Test(dataProvider = "prepare_InputAndExpectedOutput_for_response")
    public void respondTest(String input, String output)
    {
        xLogger.debug("{} Starting the execution of test_response", LOGGING_BASE_VALUE);
        JsonObject actualInput = new Gson().fromJson(input, JsonObject.class);
        JsonObject expectedOutput = new Gson().fromJson(output, JsonObject.class);
        JsonObject actualOutput = new JsonObject();
        try {
            actualOutput = api.respond(actualInput);
            xLogger.debug("{} ActualOutput: {}", LOGGING_BASE_VALUE, actualOutput.toString());
            xLogger.debug("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput.toString());
            JSONAssert.assertEquals(expectedOutput.toString(), actualOutput.toString(), JSONCompareMode.LENIENT);
            xLogger.debug("passed test_respond");
        } catch (JSONException e) {
            xLogger.error("{} Failed to parse results to JSON: {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} Failed test_respond", LOGGING_BASE_VALUE);
        } catch (AssertionError e) {
            xLogger.error("{} Assertion Error {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput);
            xLogger.error("{} ActualResult: {}", LOGGING_BASE_VALUE, actualOutput);
            xLogger.error("{} Failed test_dataRetrieve {}", LOGGING_BASE_VALUE);
            throw e;
        }
    }

    /*
     * This Method will give the input and expected output for respond method
     */

    @DataProvider(name = "prepare_InputAndExpectedOutput_for_response")
    private Object[][] prepare_InputAndExpectedOutput_for_response() {
        xLogger.debug("Starting prepare_InputAndExpectedOutput_for_respond");
        return new Object[][]{
                new Object[]{
                        "{\"authorisationDetails\":[{\"authenticationAndAuthorizationSourceSytem\":{\"username\":\"admin\",\"password\":\"admin123\"}}]}",
                        "{\"authorisationDetails\":[{\"authenticationAndAuthorizationSourceSytem\":{\"username\":\"admin\",\"password\":\"admin123\"}}]}"
                }
        };
    }
}
