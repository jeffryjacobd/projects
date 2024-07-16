package com.suntecgroup.maven.plugin.bitbucket.coverage.reader;

import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.Report;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;

public interface JacocoXmlReader {
    Report getJacocoReport(final Path reportPath);
}
