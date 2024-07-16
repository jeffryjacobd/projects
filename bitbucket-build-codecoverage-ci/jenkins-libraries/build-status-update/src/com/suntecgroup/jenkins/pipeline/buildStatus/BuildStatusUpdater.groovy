package com.suntecgroup.jenkins.pipeline.buildStatus


import com.suntecgroup.jenkins.pipeline.buildStatus.enumerations.BuildStatus

interface BuildStatusUpdater {

    void updateBuildStatus(final BuildStatus status)

    void setScript(final def script)

}