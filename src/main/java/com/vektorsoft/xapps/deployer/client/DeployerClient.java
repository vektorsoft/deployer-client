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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class DeployerClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeployerClient.class);

	public void deploy(File configFile, String serverUrl) throws DeployerException {
		LOGGER.info("Started application deployment");
		XmlDataExtractor extractor = new XmlDataExtractor();
		String appXml = extractor.extractApplicationData(configFile);
		String applicationId = extractor.extractApplicationId(configFile);

		SimpleHttpClient client = new SimpleHttpClient(serverUrl, applicationId);
		String configResponse = client.uploadConfig(appXml);
		if(configResponse != null) {
			LOGGER.info("Configuration response received, starting packaging deployment archive");
			DeploymentPackager packager = new DeploymentPackager(extractor.extractApplicationId(configFile), configFile.getParentFile());
			File archive = packager.pack(configResponse);
			LOGGER.info("Deployment archive created successfully");
			String trackUrl = client.uploadContent(archive);
			LOGGER.info("Deployment archive upload accepted by server. Status URL: {}", trackUrl);
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
		DeployerClient client = new DeployerClient();
		File file = new File(args[0]);
		client.deploy(file, args[1]);
	}


}
