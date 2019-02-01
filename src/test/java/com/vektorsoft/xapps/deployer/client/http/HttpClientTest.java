/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.http;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.vektorsoft.xapps.deployer.client.xml.XmlDataExtractor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class HttpClientTest {

	private static final String SERVER_URL = "http://localhost:8080";
	private static final String CONFIG_FILE_NAME = "/deployer-config.xml";

	@Rule
	public WireMockRule server = new WireMockRule();

	private SimpleHttpClient client;
	private String appId;
	private String configXml;

	@Before
	public void setup() throws Exception {
		appId = UUID.randomUUID().toString();
		client = new SimpleHttpClient(SERVER_URL, appId);

		var fileUrl = getClass().getResource(CONFIG_FILE_NAME);
		var configFile = new File(fileUrl.toURI());
		var extractor = new XmlDataExtractor();
		configXml = extractor.extractApplicationData(configFile);
	}

	@Test
	public void testConfigUploadSuccess() throws Exception {
		stubFor(put(urlMatching("/.*/config")).willReturn(aResponse().withBody("test").withStatus(200)));

		client.uploadConfig(configXml);
	}
}
