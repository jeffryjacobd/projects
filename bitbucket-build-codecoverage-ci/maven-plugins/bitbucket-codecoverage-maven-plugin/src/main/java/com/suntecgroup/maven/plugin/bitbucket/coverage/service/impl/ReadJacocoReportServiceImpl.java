package com.suntecgroup.maven.plugin.bitbucket.coverage.service.impl;

import com.suntecgroup.maven.plugin.bitbucket.coverage.context.CoveragePublishMojoContext;
import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.CommitCoverageEntity;
import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.FileCoverageEntity;
import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.*;
import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.Package;
import com.suntecgroup.maven.plugin.bitbucket.coverage.reader.JacocoXmlReader;
import com.suntecgroup.maven.plugin.bitbucket.coverage.service.ReadJacocoReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.project.MavenProject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Named
@Singleton
public class ReadJacocoReportServiceImpl implements ReadJacocoReportService {

    private final JacocoXmlReader jacocoXmlReader;
    private final CoveragePublishMojoContext context;

    @Inject
    public ReadJacocoReportServiceImpl(final @Named CoveragePublishMojoContext context,
                                       final @Named JacocoXmlReader jacocoXmlReader) {
        this.context = context;
        this.jacocoXmlReader = jacocoXmlReader;
    }

    @Override
    public CommitCoverageEntity getCommitCoverageEntity(Set<String> differenceFiles) {
        context.getLog().info("Creating code coverage entities");
        final CommitCoverageEntity codeCoverageOutput = differenceFiles.stream()
                .map(this::separateOnSubModuleLevel)
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue,
                                Collectors.toList())))
                .entrySet().stream()
                .flatMap(this::getFileCoverageEntityInSubModules)
                .collect(Collectors.collectingAndThen(Collectors.toList(),
                        fileCoverageEntityList -> {
                            final CommitCoverageEntity commitCoverageEntity = new CommitCoverageEntity();
                            commitCoverageEntity.setFiles(fileCoverageEntityList);
                            return commitCoverageEntity;
                        }));
        context.getLog().info("Created code coverage entities");
        return codeCoverageOutput;
    }

    private Stream<FileCoverageEntity> getFileCoverageEntityInSubModules(final
                                                                         Map.Entry<List<MavenProject>,
                                                                                 List<List<String>>> moduleListFilesListPathListEntry) {
        final MavenProject lastSubModuleMavenProject = moduleListFilesListPathListEntry.getKey()
                .stream()
                .reduce((first, second) -> second).orElseGet(context::getMavenProject);
        final Path jacocoReportPath = Paths.get(lastSubModuleMavenProject.getBasedir().getAbsolutePath(),
                "target",
                "site",
                "jacoco",
                "jacoco.xml");
        final AtomicReference<Map.Entry<MavenProject, Report>> reportReference = new AtomicReference<>();
        return moduleListFilesListPathListEntry.getValue().stream()
                .filter(filePath -> Files.exists(jacocoReportPath))
                .peek(filePath -> reportReference.compareAndSet(null,
                        new AbstractMap.SimpleImmutableEntry<>(lastSubModuleMavenProject,
                                jacocoXmlReader.getJacocoReport(jacocoReportPath))))
                .map(filePath -> new AbstractMap.SimpleImmutableEntry<>(filePath, reportReference.get()))
                .flatMap(this::getFileCoverageEntity);
    }

    private Stream<FileCoverageEntity> getFileCoverageEntity(
            final Map.Entry<List<String>, Map.Entry<MavenProject, Report>> filePathListProjectReportEntryEntry) {
        final List<String> filePathInModule = filePathListProjectReportEntryEntry.getKey();
        final MavenProject subModuleProject = filePathListProjectReportEntryEntry.getValue().getKey();
        final Report jacocoReport = filePathListProjectReportEntryEntry.getValue().getValue();
        final List<String> relativeSourceDirectory = getRelativeSourceDirectory(subModuleProject);
        if (isFilePathInSourceDirectory(filePathInModule, relativeSourceDirectory)) {
            final List<String> filePathInModuleSource = filePathInModule.subList(relativeSourceDirectory.size(), filePathInModule.size());
            final String packageName = removeLastIfMoreThanOneWithPatternSplitAndJoin(filePathInModuleSource,
                    "/");
            final String classNameWithExtension = filePathInModuleSource.get(filePathInModuleSource.size() - 1);
            final String classNameWithoutExtension = removeExtensionInClassName(classNameWithExtension);
            return processJacocoReportForOneFile(jacocoReport,
                    packageName,
                    classNameWithoutExtension,
                    classNameWithExtension,
                    filePathInModule,
                    subModuleProject);
        }
        return Stream.empty();
    }

    private Stream<FileCoverageEntity> processJacocoReportForOneFile(Report jacocoReport, String packageName, String classNameWithoutExtension, String classNameWithExtension, List<String> filePathInModule, MavenProject subModuleProject) {
        return jacocoReport.getGroupOrPackage().stream()
                .filter(Package.class::isInstance)
                .map(Package.class::cast)
                .filter(classPackage -> StringUtils.equals(classPackage.getName(), packageName))
                .flatMap(classPackage -> classPackage.getClazzOrSourcefile().stream())
                .flatMap(classOrSource -> processJacocoClassOrSource(classOrSource,
                        packageName,
                        classNameWithoutExtension,
                        classNameWithExtension))
                .reduce((oldCoverageMap, newCoverageMap) -> {
                    newCoverageMap.forEach((coverageType, lines) -> oldCoverageMap.compute(coverageType,
                            (coverageTypeLocal, valueList) -> Optional.ofNullable(valueList)
                                    .map(oldValueList -> {
                                        oldValueList.addAll(lines);
                                        return oldValueList;
                                    }).orElseGet(() -> new HashSet<>(lines))));
                    return oldCoverageMap;
                }).map(coverageMap -> convertCoverageMapToFileCoverageEntity(coverageMap, filePathInModule, subModuleProject))
                .map(Stream::of)
                .orElseGet(Stream::empty);
    }

    private Stream<Map<String, Set<Integer>>> processJacocoClassOrSource(final Serializable classOrSource,
                                                                         final String packageName,
                                                                         final String classNameWithoutExtension,
                                                                         final String classNameWithExtension) {
        if (classOrSource instanceof com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.Class) {
            final com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.Class jacocoClass =
                    (com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.Class) classOrSource;
            if (jacocoClass.getName().equals(packageName.concat("/").concat(classNameWithoutExtension))) {
                return readCoverageDataFromJacocoClass(jacocoClass);
            }
        } else if (classOrSource instanceof Sourcefile) {
            final Sourcefile jacocoSource = (Sourcefile) classOrSource;
            if (jacocoSource.getName().equals(classNameWithExtension)) {
                return readCoverageDataFromJacocoSourceFile(jacocoSource);
            }
        }
        return Stream.empty();
    }

    private FileCoverageEntity convertCoverageMapToFileCoverageEntity(final Map<String, Set<Integer>> coverageMap,
                                                                      final List<String> filePathInModule,
                                                                      final MavenProject subModuleProject) {
        final List<String> pathFromRootModule = new ArrayList<>(Arrays.asList(
                StringUtils.split(
                        Paths.get(context.getMavenProject().getBasedir().getAbsolutePath())
                                .relativize(Paths.get(subModuleProject.getBasedir().getAbsolutePath()))
                                .toString(),
                        File.separator)));
        pathFromRootModule.addAll(filePathInModule);
        final String filePathFromBaseProject = String.join("/", pathFromRootModule);
        final String coverageString = coverageMap.entrySet().stream()
                .map(keyValueSetEntry -> keyValueSetEntry.getKey()
                        .concat(":")
                        .concat(keyValueSetEntry.getValue().stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(","))))
                .collect(Collectors.joining(";"));
        final FileCoverageEntity fileCoverageEntity = new FileCoverageEntity();
        fileCoverageEntity.setCoverage(coverageString);
        fileCoverageEntity.setPath(filePathFromBaseProject);
        return fileCoverageEntity;
    }

    private Stream<Map<String, Set<Integer>>> readCoverageDataFromJacocoClass(
            final com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.Class jacocoClass) {
        final AtomicInteger methodLineNumber = new AtomicInteger();
        return jacocoClass.getMethod().stream()
                .peek(method -> methodLineNumber.set(new Integer(method.getLine())))
                .map(Method::getCounter)
                .flatMap(List::stream)
                .filter(counter -> counter.getType().equals("LINE"))
                .map(this::getMethodCounterType)
                .map(counterType -> new AbstractMap.SimpleImmutableEntry<>(counterType, methodLineNumber.get()))
                .collect(getMapKeyGroupingToListValueCollector());
    }

    private Collector<Map.Entry<String, Integer>, ?, Stream<Map<String, Set<Integer>>>> getMapKeyGroupingToListValueCollector() {
        return Collectors.collectingAndThen(
                Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue,
                                Collectors.toSet())),
                Stream::of);
    }

    private String getMethodCounterType(final Counter counter) {
        final int missed = new Integer(counter.getMissed());
        final int covered = new Integer(counter.getCovered());
        if (missed == 0 && covered > 0) {
            return "C";
        } else if (missed > 0 && covered > 0) {
            return "P";
        } else if (missed > 0 && covered == 0) {
            return "U";
        } else {
            return "unknown";
        }
    }

    private Stream<Map<String, Set<Integer>>> readCoverageDataFromJacocoSourceFile(final Sourcefile jacocoSource) {
        return jacocoSource.getLine().stream()
                .map(line -> {
                    final int coveredBranch = new Integer(line.getCb());
                    final int missedBranch = new Integer(line.getMb());
                    final int missedInstruction = new Integer(line.getMi());
                    final int lineNumber = new Integer(line.getNr());
                    if (missedBranch == 0 && coveredBranch == 0) {
                        if (missedInstruction == 0) {
                            return new AbstractMap.SimpleImmutableEntry<>("C", lineNumber);
                        } else {
                            return new AbstractMap.SimpleImmutableEntry<>("U", lineNumber);
                        }
                    } else if (missedBranch != 0 && coveredBranch != 0) {
                        return new AbstractMap.SimpleImmutableEntry<>("P", lineNumber);
                    } else if (missedInstruction == 0) {
                        return new AbstractMap.SimpleImmutableEntry<>("C", lineNumber);
                    } else {
                        return new AbstractMap.SimpleImmutableEntry<>("U", lineNumber);
                    }
                }).collect(getMapKeyGroupingToListValueCollector());
    }

    private String removeLastIfMoreThanOneWithPatternSplitAndJoin(final List<String> stringList,
                                                                  final String joinPattern) {
        final Deque<String> stringDeque = new ArrayDeque<>(stringList);
        if (stringDeque.size() > 1) {
            stringDeque.removeLast();
            return String.join(joinPattern, stringDeque);
        }
        return stringDeque.getFirst();
    }

    private String removeExtensionInClassName(final String classNameWithExtension) {
        return removeLastIfMoreThanOneWithPatternSplitAndJoin(
                Arrays.asList(
                        StringUtils.split(classNameWithExtension, ".")), "");
    }

    private boolean isFilePathInSourceDirectory(List<String> filePathInModule, List<String> relativeSourceDirectory) {
        return filePathInModule.subList(0, relativeSourceDirectory.size()).equals(relativeSourceDirectory);
    }

    private List<String> getRelativeSourceDirectory(final MavenProject mavenProject) {
        return Arrays.asList(
                StringUtils.split(
                        Paths.get(mavenProject.getBasedir().getPath())
                                .relativize(Paths.get(mavenProject.getBuild().getSourceDirectory()))
                                .toString()
                        , File.separator));
    }

    private Map.Entry<List<MavenProject>, List<String>> separateOnSubModuleLevel(final String differenceFile) {
        final AtomicReference<MavenProject> currentProject = new AtomicReference<>(context.getMavenProject());
        final List<String> fileSplitWithSeparatorList = Arrays.asList(differenceFile.split("/"));
        final Deque<String> fileSplitWithSeparatorDeque = new ArrayDeque<>(fileSplitWithSeparatorList);
        final List<MavenProject> moduleList = fileSplitWithSeparatorList.stream()
                .filter(filePathName -> currentProject.get().getModules().contains(filePathName))
                .peek(moduleName -> fileSplitWithSeparatorDeque.removeFirst())
                .map(moduleName -> currentProject.updateAndGet(currentProjectLocal ->
                        currentProjectLocal.getCollectedProjects()
                                .stream()
                                .filter(mavenProject -> Paths.get(currentProjectLocal.getBasedir().getPath())
                                        .relativize(Paths.get(mavenProject.getBasedir().getPath()))
                                        .toString().equals(moduleName))
                                .findFirst()
                                .orElseThrow(RuntimeException::new)))
                .collect(Collectors.toList());
        return new AbstractMap.SimpleImmutableEntry<>(moduleList, new ArrayList<>(fileSplitWithSeparatorDeque));
    }
}
