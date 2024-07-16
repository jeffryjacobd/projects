package com.suntecgroup.maven.plugin.bitbucket.coverage.entity;

import lombok.Data;

@Data
public class FileCoverageEntity {
    private String path;

    private String coverage;
}
