/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.http;


import com.vektorsoft.xapps.deployer.client.DeployerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.vektorsoft.xapps.deployer.client.http.HttpConstants.*;

/**
 * HTTP client for communication with server.
 *
 */
public class SimpleHttpClient {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClient.class);
	private static final int HTTP_STATUS_OK = 200;
	private static final int HTTP_STATUS_ACCEPTED = 202;

	private final HttpClient httpClient;
	private final String serverUrl;
	private final String applicationId;

	public SimpleHttpClient(String serverUrl, String applicationId) {
		httpClient = HttpClient.newBuilder().build();
		this.serverUrl = serverUrl;
		this.applicationId = applicationId;
	}

	/**
	 * Uploads project configuration file to server.
	 *
	 * @param configXml configuration XML as text
	 * @return processed configuration from server
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String uploadConfig(String configXml) throws DeployerException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(configUploadUrl(serverUrl, applicationId)))
				.setHeader(HEADER_CONTENT_TYPE, DEPLOYMENT_CONFIG_CONTENT_TYPE)
				.PUT(HttpRequest.BodyPublishers.ofString(configXml))
				.build();

		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() == HTTP_STATUS_OK) {
				LOGGER.debug("Received successful response for configuration deployment");
				return response.body();
			}
		} catch (IOException | InterruptedException ex) {
			throw new DeployerException(ex);
		}
		return null;

	}

	/**
	 * Upload deployment content archive to server. Archive is supposed to be in .zip format.
	 *
	 * @param content ZIP archive with deployment content
	 * @return URL which can be used to track deployment status on server
	 * @throws DeployerException if an error occurs
	 */
	public String uploadContent(File content) throws DeployerException {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(contentUploadUrl(serverUrl, applicationId)))
					.setHeader(HEADER_CONTENT_TYPE, DEPLOYMENT_DATA_CONTENT_TYPE)
					.PUT(HttpRequest.BodyPublishers.ofFile(content.toPath()))
					.build();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() == HTTP_STATUS_ACCEPTED) {
				LOGGER.debug("Received HTTP 202 (Accepted) status from server");
				return response.headers().firstValue(HEADER_DEPLOYMENT_STATUS).get();
			} else {
				throw new DeployerException("Received invalid status code from server: " + response.statusCode());
			}
		} catch(IOException | InterruptedException ex) {
			throw new DeployerException(ex);
		}

	}


}
