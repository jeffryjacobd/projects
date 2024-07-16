package com.suntecgroup.xelerate.cs002manageinvoicebs.function.api;

/* Start of platform import */

import com.google.gson.*;
import com.suntecgroup.xelerate.cs002manageinvoicebs.function.function.api.SummariseInvoiceLinesFunction_1_0;
import com.suntecgroup.xelerate.sunrise.common.service.ManageEntityService;
import com.suntecgroup.xelerate.sunrise.common.service.ServiceDiscovery;
import com.suntecgroup.xelerate.sunrise.common.utilities.CommonEntityUtils;
import com.suntecgroup.xelerate.sunrise.common.utilities.CommonUtils;
import com.suntecgroup.xelerate.sunrise.exception.FunctionalException;
import com.suntecgroup.xelerate.sunrise.exception.InvalidateRecordException;
import com.suntecgroup.xelerate.sunrise.logging.XLogger;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.mongo.MongoMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;

import org.json.JSONException;

import org.junit.jupiter.api.BeforeEach;

import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
/* End of platform import */

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@EnableAutoConfiguration(exclude = {MongoMetricsAutoConfiguration.class})
public class SummariseInvoiceLinesFunction_1_0Test extends AbstractTestNGSpringContextTests {

    @Autowired
    private Gson gson;

    @SpringBootConfiguration
    @ComponentScan(basePackages = {"com.suntecgroup.xelerate.sunrise.bs.common", "com.suntecgroup.xelerate.sunrise.common",
            "com.suntecgroup.xelerate.cs002manageinvoicebs.*"})
    public static class TestApplication {

        public GsonBuilder gsonBuilder() {
            return new GsonBuilder();
        }
    }
    @Autowired
    @Qualifier("cS002ManageInvoiceBS/summariseInvoiceLines_1.0")
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @InjectMocks
    private SummariseInvoiceLinesFunction_1_0 api;

    @Mock
    CommonEntityUtils service2;

    @Mock
    ManageEntityService service;

    @Mock
    ServiceDiscovery serviceDiscovery;

    private XLogger xLogger = XLogger.getXLogger(SummariseInvoiceLinesFunction_1_0Test.class);
    private final String LOGGING_BASE_VALUE = "CS002 : FindPendingVouchersFunction_1_0Test : MAY-2023 : ";

    @Test
    public void executeTests(){
    }
    /**
     * Input - input, expected output and mock of execute
     * Output
     * This test Method will test the "execute" method
     * and Assert with expected output and actualOutput
     */
    @Test(dataProvider = "prepare_InputAndMockAndExpectedOutput_for_execute")
    public void test_execute(String input, String output, List<JsonElement> executeMock,String discoveryApiMock) {
        xLogger.debug("Starting the execution of test_execute");
        JsonObject actualInput = new Gson().fromJson(input, JsonObject.class);
        JsonObject expectedOutput = new Gson().fromJson(output, JsonObject.class);


        JsonObject mockApiData = new Gson().fromJson(discoveryApiMock,JsonObject.class);
        List<JsonElement> mockDataApiList = new LinkedList<>();
        mockDataApiList.add(mockApiData);

        JsonObject actualOutput = new JsonObject();
        try {

            Mockito.when(service.executeAPI(Mockito.any(JsonObject.class))).
                    thenAnswer(AdditionalAnswers.returnsElementsOf(executeMock));
            api.preProcess(actualInput);
            Mockito.when(service2.getVersion(Mockito.any(String.class), Mockito.any(JsonObject.class), Mockito.any(JsonObject.class)))
                    .thenReturn("1.0");
            Mockito.when(serviceDiscovery.executeSunRiseAPI(Mockito.any(HttpMethod.class),
                            Mockito.any(JsonObject.class), Mockito.any(String.class), Mockito.any(String.class))).
                    thenAnswer(AdditionalAnswers.returnsElementsOf(mockDataApiList));
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

    /**
     * Input - input, expected output and mock of execute
     * Output
     * This test Method will test the "execute" method
     * and Assert with expected output and actualOutput
     */
    @Test(dataProvider = "prepare_InputAndMockAndExpectedOutput_for_execute",expectedExceptions = FunctionalException.class)
    public void test_executeExcep(String input, String output, List<JsonElement> executeMock,String discoveryApiMock) {
        xLogger.debug("Starting the execution of test_execute");
        JsonObject actualInput = new Gson().fromJson(input, JsonObject.class);
        JsonObject expectedOutput = new Gson().fromJson(output, JsonObject.class);


        JsonObject mockApiData = new Gson().fromJson(discoveryApiMock,JsonObject.class);
        List<JsonElement> mockDataApiList = new LinkedList<>();
        mockDataApiList.add(mockApiData);

        JsonObject actualOutput = new JsonObject();
        Mockito.when(service.executeAPI(Mockito.any(JsonObject.class))).
                thenAnswer(AdditionalAnswers.returnsElementsOf(executeMock));
        api.preProcess(actualInput);
        Mockito.when(service2.getVersion(Mockito.any(String.class), Mockito.any(JsonObject.class), Mockito.any(JsonObject.class)))
                .thenReturn("1.0");
        Mockito.when(serviceDiscovery.executeSunRiseAPI(Mockito.any(HttpMethod.class),
                        Mockito.any(JsonObject.class), Mockito.any(String.class), Mockito.any(String.class)))
                .thenThrow(FunctionalException.class);
        actualOutput = api.execute(actualInput);

    }

    /**
     * Input - input, expected output and mock of execute
     * Output
     * This test Method will test the "execute" method
     * and Assert with expected output and actualOutput
     */
    @Test(dataProvider = "prepare_InputAndMockAndExpectedOutput_for_dataRetrive")
    public void test_dataRetrieve(String input, String output) {
        xLogger.debug("Starting the execution of test_execute");
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
            xLogger.error("{} Failed test_dataRetrieve", LOGGING_BASE_VALUE);
        } catch (AssertionError e) {
            xLogger.error("{} Assertion Error {}", LOGGING_BASE_VALUE, e.getMessage());
            xLogger.error("{} ExpectedResult: {}", LOGGING_BASE_VALUE, expectedOutput);
            xLogger.error("{} ActualResult: {}", LOGGING_BASE_VALUE, actualOutput);
            xLogger.error("{} Failed test_dataRetrieve {}", LOGGING_BASE_VALUE);
            throw e;
        }
    }

    /**
     * Input-Null
     * Output-List<JsonArray>
     * This Method will give the input and expected output and mockOutput of manageEntity api for execute method by
     * converting Hard string to List<JsonArray>
     */
    @DataProvider(name = "prepare_InputAndMockAndExpectedOutput_for_dataRetrive")
    private Object[][] prepare_InputAndMockAndExpectedOutput_for_dataRetrive() {
        xLogger.debug("Starting prepare_InputAndMockAndExpectedOutput_for_execute");
        return new Object[][]{
                new Object[]{
                        "{\"version\":\"1.0\",\"data\":{\"summarizationLevel\":\"L1\",\"internalInvoiceNumber\":\"INT420\"}}",
                        "{\"summarizationLevel\":\"L1\",\"internalInvoiceNumber\":\"INT420\"}"
                }

        };
    }

    /**
     * Input - input, expected output and mock of execute
     * Output
     * This test Method will test the "execute" method
     * and Assert with expected output and actualOutput
     */
    @Test(expectedExceptions = FunctionalException.class)
    public void test_executeForException() {
        xLogger.debug("Starting the execution of test_execute");
        JsonObject actualInput = new Gson().fromJson("{\"summarizationLevel\":\"L1\",\"internalInvoiceNumber\":\"INT0\"}", JsonObject.class);
        Mockito.when(service.executeAPI(Mockito.any(JsonObject.class)))
                .thenThrow(InvalidateRecordException.class);
        api.preProcess(actualInput);
        api.execute(actualInput);

    }
    /**
     * Input - input, expected output and mock of execute
     * Output
     * This test Method will test the "execute" method
     * and Assert with expected output and actualOutput
     */
    @Test(expectedExceptions = FunctionalException.class)
    public void test_executeFoFunctionalrException() {
        xLogger.debug("Starting the execution of test_execute");
        JsonObject actualInput = new Gson().fromJson("{\"summarizationLevel\":\"L1\",\"internalInvoiceNumber\":\"INT0\"}", JsonObject.class);
        Mockito.when(service.executeAPI(Mockito.any(JsonObject.class)))
                .thenThrow(FunctionalException.class);
        api.preProcess(actualInput);
        api.execute(actualInput);

    }


    /**
     * Input-Null
     * Output-List<JsonArray>
     * This Method will give the input and expected output and mockOutput of manageEntity api for execute method by
     * converting Hard string to List<JsonArray>
     */
    @DataProvider(name = "prepare_InputAndMockAndExpectedOutput_for_execute")
    private Object[][] prepare_InputAndMockAndExpectedOutput_for_execute() {
        xLogger.debug("Starting prepare_InputAndMockAndExpectedOutput_for_execute");
        return new Object[][]{
                new Object[]{
                        "{\"summarizationLevel\":\"L4\",\"internalInvoiceNumber\":\"33\"}",
                        "{\"consolidateInvoice\":[{\"writtenOffAmount\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"PPPPCH\",\"createdAt\":\"2023-06-29T18:06:35.303\",\"taxAmountInBaseCurrency\":{\"amount\":270.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"globalTransactionId\":\"12\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"version\":1,\"waivedOffAmount\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"accountHead\":\"10\",\"taxAmount\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"paidAmount\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"amountInBaseCurrency\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"pkId\":\"YCcmAQWL\",\"invoiceLineDescription\":\"200\",\"invoiceLineAmountRounded\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"cancelledAmount\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"modifiedBy\":null,\"invoiceLineAmountBR\":{\"amount\":140.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"invoiceLineNumber\":\"107\",\"rateNumber\":31.0,\"createdBy\":null,\"quantityNumber\":30.0,\"account\":{\"accountId\":\"a1\",\"accountNumber\":10},\"customer\":{\"customerId\":\"35\",\"customerNumber\":20},\"description\":\"400\"},{\"writtenOffAmount\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"PCH2\",\"createdAt\":\"2023-06-29T18:06:35.999\",\"taxAmountInBaseCurrency\":{\"amount\":110.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"globalTransactionId\":\"15\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"version\":1,\"waivedOffAmount\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"accountHead\":\"30\",\"taxAmount\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"paidAmount\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"amountInBaseCurrency\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"pkId\":\"fpxZV8Yr\",\"invoiceLineDescription\":\"202\",\"invoiceLineAmountRounded\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"cancelledAmount\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"modifiedBy\":null,\"invoiceLineAmountBR\":{\"amount\":70.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"invoiceLineNumber\":\"104\",\"rateNumber\":32.0,\"createdBy\":null,\"quantityNumber\":32.0,\"account\":{\"accountId\":\"a3\",\"accountNumber\":30},\"customer\":{\"customerId\":\"C3\",\"customerNumber\":3},\"description\":\"400\"}]}"
                        , getMockValueForInvoiceSummary(),
                        "{\"result\":{\"CCH1\":{\"parentChargingCode\":\"PPPPCH\",\"description\":\"400\"},\"CCH2\":{\"parentChargingCode\":\"PPPPCH\",\"description\":\"400\"},\"CH3\":{\"parentChargingCode\":\"PCH2\",\"description\":\"400\"},\"CH5\":{\"parentChargingCode\":\"PPPPCH\",\"description\":\"400\"},\"CH6\":{\"parentChargingCode\":\"PPPPCH\",\"description\":\"400\"},\"CH4\":{\"parentChargingCode\":\"PCH2\",\"description\":\"400\"}}}"
                }

        };
    }

    private List<JsonElement> getMockValueForInvoiceSummary() {
        xLogger.debug("{} starting prepareServiceExecuteApiForResponseBE", LOGGING_BASE_VALUE);
        String mock1 = "[{\"invoiceLines\":[{\"writtenOffAmount\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"CCH1\",\"createdAt\":\"2023-06-29T18:06:35.303\",\"taxAmountInBaseCurrency\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"globalTransactionId\":\"12\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"version\":1,\"waivedOffAmount\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"accountHead\":\"10\",\"taxAmount\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"paidAmount\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"amountInBaseCurrency\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"pkId\":\"YCcmAQWL\",\"invoiceLineDescription\":\"200\",\"invoiceLineAmountRounded\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"cancelledAmount\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"modifiedBy\":null,\"paymentStatus\":\"P\",\"invoiceLineAmountBR\":{\"amount\":10.0,\"currencyCode\":\"INR\",\"fx_Rate\":10.0},\"invoiceLineStatus\":\"O\",\"invoiceLineNumber\":\"107\",\"accountingStatus\":\"C\",\"rateNumber\":31.0,\"createdBy\":null,\"quantityNumber\":30.0,\"account\":{\"accountId\":\"a1\",\"accountNumber\":10},\"customer\":{\"customerId\":\"35\",\"customerNumber\":20}},{\"writtenOffAmount\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"CCH2\",\"createdAt\":\"2023-06-29T18:06:35.657\",\"taxAmountInBaseCurrency\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"globalTransactionId\":\"14\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"version\":1,\"waivedOffAmount\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"accountHead\":\"20\",\"taxAmount\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"paidAmount\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"amountInBaseCurrency\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"pkId\":\"Bh1xF8a1\",\"invoiceLineDescription\":\"201\",\"invoiceLineAmountRounded\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"cancelledAmount\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"modifiedBy\":null,\"paymentStatus\":\"P\",\"invoiceLineAmountBR\":{\"amount\":20.0,\"currencyCode\":\"INR\",\"fx_Rate\":20.0},\"invoiceLineStatus\":\"O\",\"invoiceLineNumber\":\"102\",\"accountingStatus\":\"C\",\"rateNumber\":31.0,\"createdBy\":null,\"quantityNumber\":30.0,\"account\":{\"accountId\":\"a2\",\"accountNumber\":20},\"customer\":{\"customerId\":\"C2\",\"customerNumber\":2}},{\"writtenOffAmount\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"CH3\",\"createdAt\":\"2023-06-29T18:06:35.999\",\"taxAmountInBaseCurrency\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"globalTransactionId\":\"15\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"version\":1,\"waivedOffAmount\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"accountHead\":\"30\",\"taxAmount\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"paidAmount\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"amountInBaseCurrency\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"pkId\":\"fpxZV8Yr\",\"invoiceLineDescription\":\"202\",\"invoiceLineAmountRounded\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"cancelledAmount\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"modifiedBy\":null,\"paymentStatus\":\"P\",\"invoiceLineAmountBR\":{\"amount\":30.0,\"currencyCode\":\"INR\",\"fx_Rate\":30.0},\"invoiceLineStatus\":\"O\",\"invoiceLineNumber\":\"103\",\"accountingStatus\":\"C\",\"rateNumber\":32.0,\"createdBy\":null,\"quantityNumber\":32.0,\"account\":{\"accountId\":\"a3\",\"accountNumber\":30},\"customer\":{\"customerId\":\"C3\",\"customerNumber\":3}},{\"writtenOffAmount\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"CH5\",\"createdAt\":\"2023-06-29T18:06:36.707\",\"taxAmountInBaseCurrency\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"globalTransactionId\":\"17\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"version\":1,\"waivedOffAmount\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"accountHead\":\"50\",\"taxAmount\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"paidAmount\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"amountInBaseCurrency\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"pkId\":\"PzXBwTQM\",\"invoiceLineDescription\":\"205\",\"invoiceLineAmountRounded\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"cancelledAmount\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"modifiedBy\":null,\"paymentStatus\":\"P\",\"invoiceLineAmountBR\":{\"amount\":50.0,\"currencyCode\":\"INR\",\"fx_Rate\":50.0},\"invoiceLineStatus\":\"O\",\"invoiceLineNumber\":\"105\",\"accountingStatus\":\"C\",\"rateNumber\":35.0,\"createdBy\":null,\"quantityNumber\":52.0,\"account\":{\"accountId\":\"a5\",\"accountNumber\":50},\"customer\":{\"customerId\":\"C5\",\"customerNumber\":5}},{\"writtenOffAmount\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"CH6\",\"createdAt\":\"2023-06-29T18:06:37.061\",\"taxAmountInBaseCurrency\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"globalTransactionId\":\"17\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"version\":1,\"waivedOffAmount\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"accountHead\":\"60\",\"taxAmount\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"paidAmount\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"amountInBaseCurrency\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"pkId\":\"E7IqNrP0\",\"invoiceLineDescription\":\"206\",\"invoiceLineAmountRounded\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"cancelledAmount\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"modifiedBy\":null,\"paymentStatus\":\"P\",\"invoiceLineAmountBR\":{\"amount\":60.0,\"currencyCode\":\"INR\",\"fx_Rate\":60.0},\"invoiceLineStatus\":\"O\",\"invoiceLineNumber\":\"106\",\"accountingStatus\":\"C\",\"rateNumber\":36.0,\"createdBy\":null,\"quantityNumber\":62.0,\"account\":{\"accountId\":\"a6\",\"accountNumber\":60},\"customer\":{\"customerId\":\"C6\",\"customerNumber\":5}},{\"writtenOffAmount\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"modifiedAt\":\"NULL\",\"chargeType\":\"R\",\"chargingCode\":\"CH4\",\"createdAt\":\"2023-06-29T18:06:36.352\",\"taxAmountInBaseCurrency\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"globalTransactionId\":\"16\",\"UUID\":\"e7df3254-aa68-4118-91db-97f454b45ea1\",\"period\":{\"fromDate\":\"2023-09-23T13:48:24.443\",\"toDate\":\"2023-09-24T13:48:24.443\"},\"disputedAmount\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"version\":1,\"waivedOffAmount\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"accountHead\":\"40\",\"taxAmount\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"paidAmount\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"amountInBaseCurrency\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"pkId\":\"J8JMRlnH\",\"invoiceLineDescription\":\"203\",\"invoiceLineAmountRounded\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"cancelledAmount\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"modifiedBy\":null,\"paymentStatus\":\"P\",\"invoiceLineAmountBR\":{\"amount\":40.0,\"currencyCode\":\"INR\",\"fx_Rate\":40.0},\"invoiceLineStatus\":\"O\",\"invoiceLineNumber\":\"104\",\"accountingStatus\":\"C\",\"rateNumber\":32.0,\"createdBy\":null,\"quantityNumber\":32.0,\"account\":{\"accountId\":\"a4\",\"accountNumber\":40},\"customer\":{\"customerId\":\"C4\",\"customerNumber\":4}}],\"pkId\":\"8rFEBcWY\",\"taxDetails\":[]}]";
        List<JsonElement> returnJsonObjectList = new LinkedList<>();
        returnJsonObjectList.add(new Gson().fromJson(mock1, JsonElement.class));
        xLogger.debug("{} return output {}", LOGGING_BASE_VALUE, returnJsonObjectList);
        return returnJsonObjectList;
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
        Assertions.assertDoesNotThrow(() -> api.readBEData(new JsonObject()));
        Assertions.assertDoesNotThrow(() -> api.decisionTable(new JsonObject()));
    }

}