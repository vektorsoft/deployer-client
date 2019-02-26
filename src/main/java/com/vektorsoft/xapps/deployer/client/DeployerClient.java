/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client;

import com.vektorsoft.xapps.deployer.client.http.SimpleHttpClient;
import com.vektorsoft.xapps.deployer.client.pack.DeploymentPackager;
import com.vektorsoft.xapps.deployer.client.xml.XmlDataExtractor;

import java.io.File;
import java.nio.file.Files;

public class DeployerClient {

	public void deploy(File configFile, String serverUrl) throws DeployerException {
		XmlDataExtractor extractor = new XmlDataExtractor();
		String appXml = extractor.extractApplicationData(configFile);
		String applicationId = extractor.extractApplicationId(configFile);

		SimpleHttpClient client = new SimpleHttpClient(serverUrl, applicationId);
		String configResponse = client.uploadConfig(appXml);
		if(configResponse != null) {
			DeploymentPackager packager = new DeploymentPackager(extractor.extractApplicationId(configFile), configFile.getParentFile());
			packager.pack(configResponse);
		} else {
			throw new DeployerException("Failed to get response for configuration upload");
		}
	}

	/**
	 * Internal testing only
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		var filePath = args[0];
		File file = new File(filePath);
		XmlDataExtractor extractor = new XmlDataExtractor();
		var appId = extractor.extractApplicationId(file);
		DeploymentPackager packager = new DeploymentPackager(appId, file.getParentFile());
		String config = Files.readString(file.toPath());
		File out = packager.pack(config);
		System.out.println("Result file: " + out.getAbsolutePath());
	}


}
