package com.suntecgroup.xelerate.cs002manageinvoicebs.function.api;

import com.google.gson.GsonBuilder;
import com.suntecgroup.xelerate.cs001productbusinesstransaction.function.function.ManageCS001ProductBusinessTransaction_1_0EntityAPIFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.testng.annotations.Test;

@SpringBootTest
public class ManageCS001ProductBusinessTransaction_1_0FunctionAPI {

    @SpringBootConfiguration
    @ComponentScan(basePackages = {"com.suntecgroup.xelerate.*", "com.suntecgroup.xelerate.*.commons",
            "com.suntecgroup.xelerate.platform.bef.*", "com.suntecgroup.xelerate.web"})
    public static class TestApplication {
        @Bean
        public GsonBuilder gsonBuilder() {
            return new GsonBuilder();
        }
    }

    @Autowired
    ManageCS001ProductBusinessTransaction_1_0EntityAPIFunction functionAPI;

    @Test
    public void executeTests(){

    }
}
