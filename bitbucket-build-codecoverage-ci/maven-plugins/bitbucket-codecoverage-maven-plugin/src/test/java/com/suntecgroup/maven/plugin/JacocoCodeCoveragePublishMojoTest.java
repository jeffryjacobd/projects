package com.suntecgroup.maven.plugin;

import java.io.File;

import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.shared.verifier.VerificationException;
import org.apache.maven.shared.verifier.Verifier;
import org.testng.Assert;
import org.testng.TestListenerAdapter;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;

@Listeners(TestListenerAdapter.class)
public class JacocoCodeCoveragePublishMojoTest extends AbstractMojoTestCase {

	private static WireMockServer httpMockServer;

	@BeforeClass
	protected void setUp() throws Exception {
		super.setUp();
		httpMockServer = new WireMockServer(8999);
		httpMockServer
				.loadMappingsUsing(new JsonFileMappingsSource(new ClasspathFileSource("wiremock/mappings")));
		httpMockServer.start();
	}

	@AfterClass
	protected void tearDownAtEnd() throws Exception {
		httpMockServer.stop();
	}

	@Test
	public void executeCompileCheck() throws Exception {
		final File pom = getTestFile("src/test/resources/testCompile/pom.xml");
		final JacocoCodeCoveragePublishMojo codeCoverageMojo = JacocoCodeCoveragePublishMojo.class
				.cast(lookupMojo("publish", pom));
		Assert.assertNotNull(codeCoverageMojo);
		super.tearDown();
	}

	@Test(dependsOnMethods = { "executeCompileCheck" })
	public void deployMojo() throws VerificationException {
		Verifier verifier = new Verifier("./");
		verifier.addCliArgument("install");
		verifier.addCliArgument("-DskipTests");
		verifier.execute();
	}

	@Test(dependsOnMethods = { "deployMojo" })
	public void executeMojo() throws VerificationException {
		Verifier verifier = new Verifier("src/test/resources/testRun");
		verifier.addCliArgument("verify");
		verifier.addCliArgument(
				"com.suntecgroup.maven.plugin:bitbucket-codecoverage-maven-plugin:1.0-SNAPSHOT:publish");
		verifier.addCliArguments("-Dbitbucket.url=http://localhost:8999/", "-Dbitbucket.token=123",
				"-Dbitbucket.commit.id=123");
		verifier.addCliArguments("-Dbitbucket.project.key=XA", "-Dbitbucket.repository.slug=x27Assets",
				"-Dpull.request.id=1");
		verifier.execute();
	}

}
