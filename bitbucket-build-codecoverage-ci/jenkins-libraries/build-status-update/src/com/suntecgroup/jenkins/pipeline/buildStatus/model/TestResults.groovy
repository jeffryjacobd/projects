package com.suntecgroup.jenkins.pipeline.buildStatus.model

class TestResults {
    private final Integer failed
    private final Integer skipped
    private final Integer successful

    TestResults(String failed, String skipped, String successful) {
        this.failed = new Integer(failed)
        this.skipped = new Integer(skipped)
        this.successful = new Integer(successful)
    }
}