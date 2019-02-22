/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.xml;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class XmlDataExtractorTest {

	private static final String CONFIG_FILE_NAME = "/deployer-config.xml";

	private File configFile;

	@Before
	public void setup() throws Exception {
		URL fileUrl = getClass().getResource(CONFIG_FILE_NAME);
		configFile = new File(fileUrl.toURI());
	}

	@Test
	public void testAppDataExtraction() throws Exception {
		XmlDataExtractor extractor = new XmlDataExtractor();
		String out = extractor.extractApplicationData(configFile);

		assertNotNull(out);
		assertTrue(out.contains("<application"));
	}

	@Test
	public void testAppIdExtraction() throws Exception {
		XmlDataExtractor extractor = new XmlDataExtractor();
		String out = extractor.extractApplicationId(configFile);

		assertNotNull(out);
		assertEquals("1234-4566-abcd-4322", out);
	}
}
