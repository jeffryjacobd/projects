package com.suntecgroup.jenkins.pipeline.buildStatus.bitbucket

import com.suntecgroup.jenkins.pipeline.buildStatus.BuildStatusUpdater
import com.suntecgroup.jenkins.pipeline.buildStatus.enumerations.BuildStatus
import com.suntecgroup.jenkins.pipeline.buildStatus.model.BuildStatusRequest
import com.suntecgroup.jenkins.pipeline.buildStatus.model.TestResults
import groovy.json.JsonBuilder

final class BitBucketBuildStatusUpdater implements BuildStatusUpdater {

    private def script

    @Override
    void updateBuildStatus(final BuildStatus status) {
        final HttpURLConnection postRequest = populateRequestHeaderAndEndpoint()
        final BuildStatusRequest requestObject = populateBuildStatusRequestObject(status)
        postRequest.getOutputStream().write(new JsonBuilder(requestObject).toString().getBytes())
        final int responseCode = postRequest.getResponseCode()
        if (responseCode > 204) {
            throw new IllegalStateException()
        }
    }

    private HttpURLConnection populateRequestHeaderAndEndpoint() {
        final String commitId = script.env.sourceCommitID
        final String bitBucketServer = script.env.BIT_BUCKET
        final String projectKey = script.env.sourceProjectName
        final String repository = script.env.sourceRepositoryName
        final String httpAccessToken = script.env.HTTP_ACCESS_TOKEN
        final String endpoint = bitBucketServer
                .concat("/rest/api/latest/projects/")
                .concat(projectKey)
                .concat("/repos/")
                .concat(repository)
                .concat("/commits/")
                .concat(commitId)
                .concat("/builds")
        final HttpURLConnection bitbucketServerConnection = prepareBitbucketHttpConnectionRequest(endpoint)
        bitbucketServerConnection.setRequestProperty("Authorization", "Bearer ".concat(httpAccessToken))
        return bitbucketServerConnection
    }

    private BuildStatusRequest populateBuildStatusRequestObject(final BuildStatus status) {
        final String buildStatus = ((status == BuildStatus.ABORTED) ? "FAILED" : status.name())
        final String buildNumber = script.env.BUILD_NUMBER
        final String jenkinsBuildURL = script.env.RUN_DISPLAY_URL
        final String jenkinsBuildKey = script.env.JOB_NAME
        final String branchName = script.env.prFromBranchFullName
        final String testPassed = script.env.TEST_PASSED
        final String testFailed = script.env.TEST_FAILED
        final String testSkipped = script.env.TEST_SKIPPED
        final BuildStatusRequest request = new BuildStatusRequest()
        if (Objects.nonNull(testPassed) && !testPassed.isEmpty() &&
                Objects.nonNull(testFailed) && !testFailed.isEmpty() &&
                Objects.nonNull(testSkipped) && !testSkipped.isEmpty()) {
            request.setTestResults(new TestResults(testFailed, testSkipped, testPassed))
        }
        request.setRef(branchName)
        request.setUrl(jenkinsBuildURL)
        request.setKey(jenkinsBuildKey)
        request.setBuildNumber(buildNumber)
        request.setState(buildStatus)
        return request
    }

    @Override
    void setScript(final def script) {
        this.script = script
    }

    private static HttpURLConnection prepareBitbucketHttpConnectionRequest(final String endPoint) {
        final HttpURLConnection bitbucketServerConnection = (HttpURLConnection) endPoint.toURL().openConnection()
        bitbucketServerConnection.setRequestMethod("POST")
        bitbucketServerConnection.setDoOutput(true)
        bitbucketServerConnection.setRequestProperty("Content-Type", "application/json")
        return bitbucketServerConnection
    }

}
