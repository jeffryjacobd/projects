package com.suntecgroup.maven.plugin.bitbucket.coverage.service;

import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.CommitCoverageEntity;

import java.util.Set;

public interface ReadJacocoReportService {

    CommitCoverageEntity getCommitCoverageEntity(final Set<String> differenceFiles);

}
