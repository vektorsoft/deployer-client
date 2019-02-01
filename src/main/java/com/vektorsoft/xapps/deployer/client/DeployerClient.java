/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client;

import com.vektorsoft.xapps.deployer.client.http.SimpleHttpClient;
import com.vektorsoft.xapps.deployer.client.xml.XmlDataExtractor;

import java.io.File;

public class DeployerClient {

	public void deploy(File configFile, String serverUrl, String applicationId) throws DeployerException {
		XmlDataExtractor extractor = new XmlDataExtractor();
		String appXml = extractor.extractApplicationData(configFile);

		SimpleHttpClient client = new SimpleHttpClient(serverUrl, applicationId);
		client.uploadConfig(appXml);
	}


}
