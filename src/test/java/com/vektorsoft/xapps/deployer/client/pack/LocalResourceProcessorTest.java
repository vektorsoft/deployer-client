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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import java.io.File;
import java.nio.file.Path;

import static org.mockito.Mockito.*;
import  static org.junit.Assert.*;

public class LocalResourceProcessorTest {

	private static final String RESOURCE_LOCAL_PATH = "src/test/resources/image.png";
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

	@After
	public void cleanup() {
		target.delete();
	}

	@Test
	public void testProcess() throws Exception {
		when(element.getAttribute("path")).thenReturn(RESOURCE_LOCAL_PATH);
		var processor = new LocalResourceProcessor(target, source);
		processor.process(element);

		// verify that file is correctly copied
		File origin = Path.of(source.getAbsolutePath(), "src", "test", "resources", "image.png").toFile();
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

	@Test(expected = DeployerException.class)
	public void noPathAttributetest() throws Exception {
		when(element.getAttribute("path")).thenReturn(null);
		var processor = new LocalResourceProcessor(target, source);
		processor.process(element);
	}
}
