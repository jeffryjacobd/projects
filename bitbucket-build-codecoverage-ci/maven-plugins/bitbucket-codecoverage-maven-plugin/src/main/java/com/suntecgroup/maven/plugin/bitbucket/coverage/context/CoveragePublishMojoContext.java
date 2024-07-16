package com.suntecgroup.maven.plugin.bitbucket.coverage.context;

import lombok.Data;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
@Data
public class CoveragePublishMojoContext {

    private String token;
    private String commitId;
    private String project;
    private String repository;
    private String serverUrl;
    private String pullRequestId;
    private Log log;
    private MavenProject mavenProject;
}
