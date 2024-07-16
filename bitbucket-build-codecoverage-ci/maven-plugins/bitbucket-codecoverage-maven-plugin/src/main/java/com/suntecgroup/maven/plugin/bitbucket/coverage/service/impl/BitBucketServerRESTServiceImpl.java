package com.suntecgroup.maven.plugin.bitbucket.coverage.service.impl;

import com.google.gson.Gson;
import com.suntecgroup.maven.plugin.bitbucket.coverage.context.CoveragePublishMojoContext;
import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.CommitCoverageEntity;
import com.suntecgroup.maven.plugin.bitbucket.coverage.service.BitBucketServerRESTService;
import com.suntecgroup.maven.plugin.bitbucket.coverage.service.HttpClientService;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@Singleton
public class BitBucketServerRESTServiceImpl implements BitBucketServerRESTService {

    @Inject
    public BitBucketServerRESTServiceImpl(final @Named CoveragePublishMojoContext context,
                                          final @Named HttpClientService httpClientService) {
        this.context = context;
        this.httpClientService = httpClientService;
    }

    private final CoveragePublishMojoContext context;

    private final HttpClientService httpClientService;


    @Override
    public Set<String> getPullRequestDifference() throws MojoFailureException {
        context.getLog().info("Processing difference in PR");
        final String pullRequestDifferencePath = getPullRequestDifferenceRequestPath();
        final Stream<String> differenceResponseStream =
                httpClientService.sendHttpGetRequestAsLineStream(pullRequestDifferencePath);
        Set<String> differenceFiles =
                differenceResponseStream.filter(differenceResponseLine ->
                                differenceResponseLine.matches("^\\+\\+\\+ dst://.*"))
                        .map(filteredDifferenceResponseLine ->
                                filteredDifferenceResponseLine.replaceFirst("^\\+\\+\\+ dst://", ""))
                        .collect(Collectors.toSet());
        context.getLog().info("Processed difference in PR");
        return differenceFiles;
    }

    @Override
    public void postCommitCoverageEntity(CommitCoverageEntity commitCoverageEntity) throws MojoFailureException {
        context.getLog().info("Publishing code coverage to Bitbucket");
        final String postCoveragePath = getPostCoveragePath();
        httpClientService.sendPostRequest(postCoveragePath,new Gson().toJson(commitCoverageEntity));
        context.getLog().info("Published code coverage to Bitbucket");
    }

    private String getPostCoveragePath() {
        return "rest/code-coverage/1.0/projects/" +
                context.getProject() +
                "/repos/" +
                context.getRepository() +
                "/commits/" +
                context.getCommitId();
    }

    private String getPullRequestDifferenceRequestPath() {
        return "rest/api/1.0/projects/" +
                context.getProject() +
                "/repos/" +
                context.getRepository() +
                "/pull-requests/" +
                context.getPullRequestId() +
                ".diff";
    }
}
