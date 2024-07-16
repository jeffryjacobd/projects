#!/usr/bin/env groovy

import com.suntecgroup.jenkins.pipeline.buildStatus.BuildStatusUpdater
import com.suntecgroup.jenkins.pipeline.buildStatus.enumerations.BuildStatus
import com.suntecgroup.jenkins.pipeline.buildStatus.bitbucket.BitBucketBuildStatusUpdater

def static call(BuildStatus buildStatus, def script) {
    BuildStatusUpdater statusUpdater = new BitBucketBuildStatusUpdater()
    statusUpdater.setScript(script)
    statusUpdater.updateBuildStatus(buildStatus)
}