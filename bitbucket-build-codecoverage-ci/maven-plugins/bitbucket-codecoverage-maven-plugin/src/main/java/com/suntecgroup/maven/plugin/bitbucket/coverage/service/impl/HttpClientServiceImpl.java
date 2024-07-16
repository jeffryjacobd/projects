package com.suntecgroup.maven.plugin.bitbucket.coverage.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.suntecgroup.maven.plugin.bitbucket.coverage.context.CoveragePublishMojoContext;
import com.suntecgroup.maven.plugin.bitbucket.coverage.service.HttpClientService;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@Singleton
public class HttpClientServiceImpl implements HttpClientService {

    @Inject
    public HttpClientServiceImpl(final @Named CoveragePublishMojoContext context) {
        this.context = context;
    }

    private final CoveragePublishMojoContext context;

    @Override
    public Stream<String> sendHttpGetRequestAsLineStream(final String path) throws MojoFailureException {
        try {
            final HttpResponse response = sendHttpGetRequestAndExtractResponse(path).returnResponse();
            throwErrorWhenStatusError(response);
            return getReaderFromHttpResponse(response).lines();
        } catch (IOException e) {
            context.getLog().error("Error in get request", e);
            throw new MojoFailureException("Error in get request", e);
        }
    }

    @Override
    public void sendPostRequest(final String path, final String requestObject) throws MojoFailureException {
        try {
            Request.Post(getServerAppendedPath(path)).addHeader("Authorization", "Bearer " + context.getToken())
                    .bodyString(requestObject, ContentType.APPLICATION_JSON).execute();
        } catch (IOException e) {
            context.getLog().error("Error in Post request", e);
            throw new MojoFailureException("Error in Post request", e);
        }
    }

    private Response sendHttpGetRequestAndExtractResponse(String path) throws IOException {
        return Request.Get(getServerAppendedPath(path)).addHeader("Authorization", "Bearer " + context.getToken())
                .execute();
    }

    private void throwErrorWhenStatusError(HttpResponse httpResponse) throws IOException {
        if (httpResponse.getStatusLine().getStatusCode() >= 300) {
            httpResponse.getEntity().getContent().close();
            context.getLog().error("Http response with Failure status code");
            throw new IOException();
        }
    }

    private BufferedReader getReaderFromHttpResponse(HttpResponse httpResponse) throws IOException {
        return new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
    }

    private String getServerAppendedPath(final String path) {
        final String serverUrl = context.getServerUrl();
        return serverUrl.endsWith("/") ? serverUrl.concat(path) : serverUrl.concat("/").concat(path);
    }
}
