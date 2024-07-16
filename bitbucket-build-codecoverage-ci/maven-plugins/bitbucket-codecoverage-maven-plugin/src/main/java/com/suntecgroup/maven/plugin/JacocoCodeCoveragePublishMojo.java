package com.suntecgroup.maven.plugin;

import com.suntecgroup.maven.plugin.bitbucket.coverage.context.CoveragePublishMojoContext;
import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.CommitCoverageEntity;
import com.suntecgroup.maven.plugin.bitbucket.coverage.service.BitBucketServerRESTService;
import com.suntecgroup.maven.plugin.bitbucket.coverage.service.ReadJacocoReportService;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Set;

@Mojo(name = "publish", requiresDirectInvocation = true, aggregator = true,
        defaultPhase = LifecyclePhase.VERIFY)
public class JacocoCodeCoveragePublishMojo extends AbstractMojo {

    @Inject
    public JacocoCodeCoveragePublishMojo(@Named final BitBucketServerRESTService bitBucketServerService,
                                         @Named final CoveragePublishMojoContext configuration,
                                         @Named final ReadJacocoReportService readJacocoReportService) {
        this.bitBucketServerService = bitBucketServerService;
        this.configuration = configuration;
        this.readJacocoReportService = readJacocoReportService;
    }

    @Parameter(property = "bitbucket.url", required = true)
    private String bitbucketUrl;


    @Parameter(property = "bitbucket.token", required = true)
    private String token;

    @Parameter(property = "bitbucket.commit.id", required = true)
    private String commitId;

    @Parameter(property = "bitbucket.project.key", required = true)
    private String projectKey;

    @Parameter(property = "bitbucket.repository.slug", required = true)
    private String repoSlug;

    @Parameter(property = "pull.request.id", required = true)
    private String pullRequestId;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject mavenProject;

    private final BitBucketServerRESTService bitBucketServerService;

    private final CoveragePublishMojoContext configuration;

    private final ReadJacocoReportService readJacocoReportService;


    @Override
    public void execute() throws MojoFailureException {
        setPluginConfiguration();
        final Set<String> filesHavingDifference = bitBucketServerService.getPullRequestDifference();
        final CommitCoverageEntity commitCoverageEntity = readJacocoReportService
                .getCommitCoverageEntity(filesHavingDifference);
        bitBucketServerService.postCommitCoverageEntity(commitCoverageEntity);
    }

    private void setPluginConfiguration() {
        configuration.setCommitId(commitId);
        configuration.setProject(projectKey);
        configuration.setToken(token);
        configuration.setRepository(repoSlug);
        configuration.setServerUrl(bitbucketUrl);
        configuration.setPullRequestId(pullRequestId);
        configuration.setLog(getLog());
        configuration.setMavenProject(mavenProject);
    }
}
