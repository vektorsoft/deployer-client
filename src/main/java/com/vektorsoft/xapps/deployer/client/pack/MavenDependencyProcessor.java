/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.pack;

import com.vektorsoft.xapps.deployer.client.DeployerException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Processor for Maven dependencies. Uses Maven coordinates of dependency to generate appropriate deployment package.
 * It is assumed that dependnecy is present in local Maven repository.
 *
 * By default, this processor assumes that local Maven repository is located at {@code {user.home.dir}/.m2/repository}. This can be overriden
 * by specifying system property {@code local.maven.repo}, whose value is absolute path to local repository.
 */
public class MavenDependencyProcessor implements ConfigElementProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(MavenDependencyProcessor.class);
	private static final DigestUtils DIGEST = new DigestUtils(MessageDigestAlgorithms.SHA_1);

	private static final String MAVEN_REPO_PROPERTY = "local.maven.repo";
	private static final String MAVEN_GROUP_ID = "groupId";
	private static final String MAVEN_ARTIFACT_ID = "artifactId";
	private static final String MAVEN_VERSION = "version";
	private static final String MAVEN_FILE_NAME = "fileName";

	private static final String MAVEN_LOCAL_REPO;

	static {
		if(System.getProperty(MAVEN_REPO_PROPERTY) != null) {
			MAVEN_LOCAL_REPO = System.getProperty(MAVEN_REPO_PROPERTY);
		} else {
			var userHomeDir = System.getProperty("user.home");
			MAVEN_LOCAL_REPO = Path.of(userHomeDir, ".m2", "repository").toString();
		}

	}

	private final File target;

	public MavenDependencyProcessor(File target) {
		this.target = target;
	}

	@Override
	public void process(Element configElement) throws DeployerException {
		String groupId = configElement.getAttribute(MAVEN_GROUP_ID);
		String artifactId = configElement.getAttribute(MAVEN_ARTIFACT_ID);
		String version = configElement.getAttribute(MAVEN_VERSION);
		String fileName = configElement.getAttribute(MAVEN_FILE_NAME);
		LOGGER.debug("Processing maven dependency: {}:{}:{}:{}", groupId, artifactId, version, fileName);


		try {
			File originFile = getDependencyFile(groupId, artifactId, version, fileName);
			String hash = DIGEST.digestAsHex(originFile);
			String[] parts = new String[]{
					hash.substring(0, 2),
					hash.substring(2, 4),
					hash.substring(4, 6)
			};
			LOGGER.debug("Found maven dependnecy hash: {}", hash);

			Path targetPath = Path.of(target.getAbsolutePath(), parts[0], parts[1], parts[2], hash);
			targetPath.toFile().mkdirs();
			Files.copy(originFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.debug("Copied dependency to file {}", targetPath.toString());
		} catch(IOException ex) {
			throw new DeployerException(ex);
		}
	}

	private File getDependencyFile(String groupId, String artifactId, String version, String fileName) {
		var list = new ArrayList<String>();
		var groupParts = groupId.split("\\.");
		Arrays.asList(groupParts).stream().forEach( p -> list.add(p));
		list.add(artifactId);
		list.add(version);
		list.add(fileName);

		return Path.of(MAVEN_LOCAL_REPO, list.toArray(new String[list.size()])).toFile();
	}

	public static String getMavenlocalRepoPath() {
		return MAVEN_LOCAL_REPO;
	}
}
