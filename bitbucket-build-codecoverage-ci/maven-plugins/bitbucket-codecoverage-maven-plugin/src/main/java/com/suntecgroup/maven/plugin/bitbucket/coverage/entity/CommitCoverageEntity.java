package com.suntecgroup.maven.plugin.bitbucket.coverage.entity;

import lombok.Data;

import java.util.List;

@Data
public class CommitCoverageEntity {

    private List<FileCoverageEntity> files;

}
