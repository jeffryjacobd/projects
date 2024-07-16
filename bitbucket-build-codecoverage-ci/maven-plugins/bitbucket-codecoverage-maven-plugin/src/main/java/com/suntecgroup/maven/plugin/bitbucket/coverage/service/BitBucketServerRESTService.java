package com.suntecgroup.maven.plugin.bitbucket.coverage.service;

import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.CommitCoverageEntity;
import org.apache.maven.plugin.MojoFailureException;

import java.util.Set;

public interface BitBucketServerRESTService {
    Set<String> getPullRequestDifference( ) throws MojoFailureException;

    void postCommitCoverageEntity(CommitCoverageEntity commitCoverageEntity) throws MojoFailureException;
}
