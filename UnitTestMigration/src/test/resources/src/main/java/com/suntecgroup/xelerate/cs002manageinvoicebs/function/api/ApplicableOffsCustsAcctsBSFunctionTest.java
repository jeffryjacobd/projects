package com.suntecgroup.xelerate.cs002manageinvoicebs.function.api;

import com.datastax.oss.driver.shaded.guava.common.reflect.TypeToken;
import com.google.gson.*;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import com.suntecgroup.xelerate.pd44431_applicableoffscustsacctsbs.function.function.api.ApplicableOffsCustsAcctsBSFunction;
import com.suntecgroup.xelerate.sunrise.common.service.ManageEntityService;
import com.suntecgroup.xelerate.sunrise.common.service.ServiceDiscovery;
import com.suntecgroup.xelerate.sunrise.common.wrapper.JDBCTemplateWrapper;
import com.suntecgroup.xelerate.sunrise.exception.FunctionalException;
import com.suntecgroup.xelerate.sunrise.logging.XLogger;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.*;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootTest
public class ApplicableOffsCustsAcctsBSFunctionTest {

    @BeforeTest
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    protected XLogger xLogger = XLogger.getXLogger(ApplicableOffsCustsAcctsBSFunctionTest.class);
    public String collectionName = "Test";
    private MongoDatabase mongoDbConnection = null;
    private MongoCollection<Document> mongoDBcollection = null;
    private MongoDatabaseUtil mongoDBUtil = null;


    @InjectMocks
    private ApplicableOffsCustsAcctsBSFunction api;

    @Mock
    ServiceDiscovery serviceDiscovery;
    @Mock
    JDBCTemplateWrapper jdbcTemplateWrapper ;
    @Mock
    ManageEntityService service;

    public ApplicableOffsCustsAcctsBSFunctionTest(){

        try {
            mongoDBUtil = MongoDatabaseUtil.getInstance();
            if(null == mongoDbConnection){
                mongoDbConnection = mongoDBUtil.getMongoDBDatabase();
            }
        } catch (Exception e) {
            xLogger.error("Exception while creating mongodb connection: "+e.getMessage());
        }

    }

    @DataProvider(name = "methodFlow")
    public Object[][] appendQueryDataProvider() {
        List<Object[]> listObj = new ArrayList<>();
        try {
            mongoDBcollection = mongoDBUtil.getMongoDBCollection(mongoDbConnection, collectionName);
            FindIterable<Document> doc = mongoDBUtil.getMongoDocumentsFind(mongoDbConnection, mongoDBcollection, collectionName);
            doc.iterator().forEachRemaining(document -> {
                Object[] arrObj = new Object[5];
                arrObj[0] = document.getString("testCaseId");
                arrObj[1] =  document.get("testData")!=null?((Document)document.get("testData")).toJson():null;
                arrObj[2] = getAsString(document.get("expectedResult"));
                arrObj[3] = convertToJsonArray((ArrayList<Document>) document.get("dbMockResult"));
                arrObj[4] = convertToJsonArray((ArrayList<Document>) document.get("apiMockResult"));
                listObj.add(arrObj);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listObj.toArray(new Object[listObj.size()][]);
    }
    @Test(dataProvider="methodFlow")
    public void methodFlowTest(String testCaseId, String input,String expectedResult, JsonArray dbMockArray,JsonArray apiMockArray){
        JsonObject inputJson = new Gson().fromJson(input, JsonObject.class);
        xLogger.debug("======="+testCaseId+"==========================================");
        xLogger.debug("dbMockResult from db:"+dbMockArray);
        xLogger.debug("apiMockResult from db:"+apiMockArray);
        String actualOutput = "";
        List<JsonElement>mockResultListForExecuteQuery=null;
        List<JsonElement>mockResultListForExecuteAPI=null;
        List<JsonElement>mockResultListForSunRiseAPICall=null;
        List<Boolean>mockResultListForExecuteQuerySave=null;
        List<JsonElement>mockResultListForSunRiseAPIExecuteAPICall=null;

        String methodName="";
        if(dbMockArray!=null) {
            for (JsonElement mock : dbMockArray) {
                for (String key:mock.getAsJsonObject().keySet() ) {
                    if(key.startsWith("method")&&key.endsWith("_to_mock")) {
                        if (mock.getAsJsonObject().get(key).getAsString().toLowerCase().equalsIgnoreCase("jdbcTemplateWrapper.executeQuery".toLowerCase())) {
                            mockResultListForExecuteQuery=getValuesAsList(mock.getAsJsonObject().getAsJsonObject("mockresult"));
                        } else if (mock.getAsJsonObject().get(key).getAsString().toLowerCase().equalsIgnoreCase("jdbcTemplateWrapper.executeQuerySave".toLowerCase())){
                            mockResultListForExecuteQuerySave =getValuesAsBooleanList(mock.getAsJsonObject().getAsJsonObject("mockresult"));
                        }else{
                            //Stub to handle other db call methods
                        }
                    }
                }
            }
        }
        if(apiMockArray!=null){
            for (JsonElement mock : apiMockArray) {
                for (String key:mock.getAsJsonObject().keySet() ) {
                    if(key.startsWith("method")&&key.endsWith("_to_mock")) {
                        if (mock.getAsJsonObject().get(key).getAsString().trim().toLowerCase().equalsIgnoreCase("service.executeAPI".toLowerCase())) {
                            mockResultListForExecuteAPI=getValuesAsList(mock.getAsJsonObject().getAsJsonObject("mockresult"));
                        } else if (mock.getAsJsonObject().get(key).getAsString().trim().toLowerCase().equalsIgnoreCase("serviceDiscovery.executeSunRiseAPI".toLowerCase())) {
                            mockResultListForSunRiseAPICall=getValuesAsList(mock.getAsJsonObject().getAsJsonObject("mockresult"));
                        }else if (mock.getAsJsonObject().get(key).getAsString().trim().toLowerCase().equalsIgnoreCase("serviceDiscovery.executeAPI".toLowerCase())) {
                            mockResultListForSunRiseAPIExecuteAPICall=getValuesAsList(mock.getAsJsonObject().getAsJsonObject("mockresult"));
                        } else {
                            //stub to handle other api calls
                        }
                    }
                }
            }
        }

        JsonObject actualResult =null;
        //JsonObject expectedJsonResult=null;
        try {
            try {
                if(mockResultListForExecuteQuery!=null) {
                    Mockito.when(jdbcTemplateWrapper.executeQuery(Mockito.anyString())).thenAnswer(AdditionalAnswers.returnsElementsOf(mockResultListForExecuteQuery));
                }
                if(mockResultListForExecuteAPI!=null){
                    Mockito.when(service.executeAPI(Mockito.any(JsonObject.class))).thenAnswer(AdditionalAnswers.returnsElementsOf(mockResultListForExecuteAPI));
                }
                if(mockResultListForSunRiseAPICall!=null){
                    Mockito.when(serviceDiscovery.executeSunRiseAPI(Mockito.any(HttpMethod.class),Mockito.any(JsonObject.class),Mockito.any(String.class))).thenAnswer(AdditionalAnswers.returnsElementsOf(mockResultListForSunRiseAPICall));
                }
                if(mockResultListForExecuteQuerySave!=null){
                   // Mockito.when(jdbcTemplateWrapper.executeQuerySave(Mockito.anyString())).thenAnswer(AdditionalAnswers.returnsElementsOf(mockResultListForExecuteQuerySave));
                }
                if(mockResultListForSunRiseAPIExecuteAPICall!=null){
                    Mockito.when(serviceDiscovery.executeAPI(Mockito.any(HttpMethod.class),Mockito.any(JsonObject.class),Mockito.any(String.class))).thenAnswer(AdditionalAnswers.returnsElementsOf(mockResultListForSunRiseAPIExecuteAPICall));
                }

                actualResult = api.methodFlow(inputJson);
                xLogger.debug("ActualResult: "+actualResult.toString());
                if(isJson(expectedResult)){
                    xLogger.debug("ExpectedResult: "+expectedResult.toString());
                    JSONAssert.assertEquals(actualResult.toString(),expectedResult, JSONCompareMode.LENIENT);
                }else{
                    xLogger.debug("ExpectedResult: "+expectedResult);
                    Assert.assertEquals(actualResult,expectedResult);
                }
            } catch (FunctionalException fe) {
                actualOutput=fe.getMessage();
                xLogger.error("ExpectedResult: "+expectedResult);
                xLogger.error("ActualResult: "+actualOutput);
                Assert.assertEquals(actualOutput, expectedResult);
            } catch (JSONException e) {
                xLogger.error("Failed to parse results to JSON: "+e.getMessage());
            }
            Bson updateFields = buildUpdateDocument(actualResult!=null? BasicDBObject.parse(actualResult.toString()):actualOutput,"Success");
            mongoDBUtil.updateMongoDocument(mongoDBcollection,testCaseId,updateFields);
        } catch (AssertionError e) {
            xLogger.error("Error while executing test for testCaseId - " + testCaseId+", Expected Result : "+expectedResult+", Actual Result : "+(actualResult!=null? BasicDBObject.parse(actualResult.toString()):actualOutput));
            Bson updateFields =  buildUpdateDocument(actualResult!=null? BasicDBObject.parse(actualResult.toString()):actualOutput,"Failure");
            mongoDBUtil.updateMongoDocument(mongoDBcollection,testCaseId,updateFields);
            throw e;
        }

    }

    private JsonArray convertToJsonArray(ArrayList<Document> inputList){
        if(inputList!=null&&inputList.size()>0){
            Gson gson=new GsonBuilder().serializeNulls().create();
            return  gson.toJsonTree(inputList.stream().map(data->
                             gson.fromJson(data.toJson(), JsonObject.class)).collect(Collectors.toList()),
                    new TypeToken<List<JsonObject>>(){}.getType()).getAsJsonArray();
        }
        return  null;
    }
    private  boolean isJson(String Json) {
        try {
            new JSONObject(Json);
        } catch (JSONException ex) {
            try {
                new JSONArray(Json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    private String getAsString(Object object){
        String result=null;
        if(object.getClass()==Document.class){
            result= ((Document)object).toJson();
        }else if(object.getClass()==String.class){
            result= object.toString();
        }
        return result;
    }

    public Bson buildUpdateDocument(Object actualResult, String status) {
        Bson updateFields = Updates.combine(
                Updates.set("executedYN", "Y"),
                Updates.set("actualResult",  actualResult),
                Updates.set("status", status),
                Updates.currentTimestamp("lastUpdated"));
        return updateFields;
    }
    private List<JsonElement> getValuesAsList(JsonObject inputJsonObject){
        return inputJsonObject!=null?inputJsonObject.entrySet()!=null?inputJsonObject.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()):null:null;
    }
    private List<Boolean> getValuesAsBooleanList(JsonObject inputJsonObject){
        return inputJsonObject!=null?inputJsonObject.entrySet()!=null?inputJsonObject.entrySet().stream().map(elementEntry -> elementEntry.getValue().getAsBoolean()).collect(Collectors.toList()):null:null;
    }
}
