package com.suntecgroup.jenkins.pipeline.buildStatus.model

class BuildStatusRequest {

    private String state
    private String url
    private String buildNumber
    private String ref
    private TestResults testResults

    BuildStatusRequest() {

    }

    BuildStatusRequest(String key, String state, String url, String buildNumber,
                       String ref, TestResults testResults) {
        this.key = key
        this.state = state
        this.url = url
        this.buildNumber = buildNumber
        this.ref = ref
        this.testResults = testResults
    }

    private String key

    void setKey(String key) {
        this.key = key
    }

    void setState(String state) {
        this.state = state
    }

    void setUrl(String url) {
        this.url = url
    }

    void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber
    }

    void setRef(String ref) {
        this.ref = ref
    }

    void setTestResults(TestResults testResults) {
        this.testResults = testResults
    }

}