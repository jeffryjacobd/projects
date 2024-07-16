package com.suntecgroup.maven.plugin.bitbucket.coverage.reader.impl;

import com.suntecgroup.maven.plugin.bitbucket.coverage.entity.jacoco.Report;
import com.suntecgroup.maven.plugin.bitbucket.coverage.reader.JacocoXmlReader;
import lombok.SneakyThrows;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Named
@Singleton
public class JacocoXmlReaderImpl implements JacocoXmlReader {
    @Override
    @SneakyThrows
    public Report getJacocoReport(final Path reportPath) {
        final SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        saxFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        saxFactory.setFeature("http://xml.org/sax/features/validation", false);
        try (final InputStream xmlStream = Files.newInputStream(reportPath)) {
            return JAXBContext.newInstance(Report.class).createUnmarshaller()
                    .unmarshal(new SAXSource(saxFactory.newSAXParser().getXMLReader(),
                            new InputSource(xmlStream)), Report.class).getValue();
        }
    }
}
