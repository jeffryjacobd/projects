package com.suntecgroup.xelerate.cs002manageinvoicebs.function.api;

/* Start of platform import */

import com.suntecgroup.xelerate.cs001managecdmbs.function.function.api.GeSubscriptionDetailsFunction_1_0;
import com.suntecgroup.xelerate.sunrise.common.service.ManageEntityService;
import com.suntecgroup.xelerate.sunrise.exception.FunctionalException;
import com.suntecgroup.xelerate.sunrise.logging.XLogger;
import com.suntecgroup.xelerate.sunrise.common.service.ServiceDiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.*;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class GeSubscriptionDetailsFunction_1_0Test extends com.suntecgroup.xelerate.sunrise.common.test.PlatformTest {

    
    private Gson gson = new Gson();
    @org.mockito.InjectMocks
    private GeSubscriptionDetailsFunction_1_0 api;

    @Test
    public void executeTests(){
    }


}
