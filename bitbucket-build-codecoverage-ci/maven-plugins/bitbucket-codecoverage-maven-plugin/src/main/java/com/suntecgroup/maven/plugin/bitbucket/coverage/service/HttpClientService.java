package com.suntecgroup.maven.plugin.bitbucket.coverage.service;

import com.google.gson.JsonObject;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;
import java.util.stream.Stream;

public interface HttpClientService {
    Stream<String> sendHttpGetRequestAsLineStream(String path) throws MojoFailureException;

    void sendPostRequest(String path, String requestObject) throws MojoFailureException;
}
