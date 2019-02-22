/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.pack;

import com.vektorsoft.xapps.deployer.client.DeployerException;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

public class MavenDependencyProcessor implements ConfigElementProcessor {

	public static final String MAVEN_REPO_PROPERTY = "local.maven.repo";

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
	private final File mavenRepo;

	public MavenDependencyProcessor(File target) {
		this.target = target;
		this.mavenRepo = new File(MAVEN_LOCAL_REPO);
	}

	@Override
	public void process(Element configElement) throws DeployerException {
		String groupId = configElement.getAttribute("groupId");
		String artifactId = configElement.getAttribute("artifactId");
		String version = configElement.getAttribute("version");
		String fileName = configElement.getAttribute("fileName");

		File originFile = getDependencyFile(groupId, artifactId, version, fileName);
		String hash = HashCalculator.fileHash(originFile);
		String[] parts = new String[]{
				hash.substring(0, 2),
				hash.substring(2, 4),
				hash.substring(4, 6)
		};

		Path targetPath = Path.of(target.getAbsolutePath(), parts[0], parts[1], parts[2], hash);
		targetPath.toFile().mkdirs();
		try {
			Files.copy(originFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
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
