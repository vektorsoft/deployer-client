/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.pack;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import java.io.File;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MavenDependencyProcessorTest {

	private static final DigestUtils DIGEST = new DigestUtils(MessageDigestAlgorithms.SHA_1);

	private File source;
	private File target;
	private Element element;

	@Before
	public void setup() {
		element = mock(Element.class);
		String workDir = System.getProperty("user.dir");
		source = new File(workDir);
		target = Path.of(workDir, "target", "content").toFile();
		if(!target.exists()) {
			target.mkdirs();
		}
	}

	@Test
	public void testMavenDependencyProcessing() throws Exception {
		when(element.getAttribute(MavenDependencyProcessor.MAVEN_GROUP_ID)).thenReturn("org.mockito");
		when(element.getAttribute(MavenDependencyProcessor.MAVEN_ARTIFACT_ID)).thenReturn("mockito-all");
		when(element.getAttribute(MavenDependencyProcessor.MAVEN_VERSION)).thenReturn("1.9.5");
		when(element.getAttribute(MavenDependencyProcessor.MAVEN_FILE_NAME)).thenReturn("mockito-all-1.9.5.jar");

		var processor = new MavenDependencyProcessor(target);
		processor.process(element);

		File origin = Path.of(MavenDependencyProcessor.getMavenlocalRepoPath(), "org", "mockito", "mockito-all", "1.9.5", "mockito-all-1.9.5.jar").toFile();
		String hash = DIGEST.digestAsHex(origin);
		String[] parts = new String[]{
				hash.substring(0, 2),
				hash.substring(2, 4),
				hash.substring(4, 6)
		};

		File output = Path.of(target.getAbsolutePath(), parts[0], parts[1], parts[2], hash).toFile();
		assertTrue(output.exists());
		// verify target hash
		String targetHash = DIGEST.digestAsHex(output);
		assertEquals(hash, targetHash);

	}
}
