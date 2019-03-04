/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.http;

/**
 * Contains constants related to HTTP operations.
 */
public final  class HttpConstants {

	private static final String DEPLOYMENT_CONFIG_URL_FORMAT = "deployment/%s/config";
	private static final String DEPLOYMENT_CONTENT_URL_FORMAT = "deployment/%s/content";

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_DEPLOYMENT_STATUS = "X-Deployment-Status";
	public static final String DEPLOYMENT_CONFIG_CONTENT_TYPE = "application/xml";
	public static final String DEPLOYMENT_DATA_CONTENT_TYPE = "application/zip";

	/**
	 * Private constructor to prevent instantiation.
	 */
	private HttpConstants() {

	}

	public static String configUploadUrl(String serverUrl, String appId) {
		return createUploadUrl(serverUrl, appId, DEPLOYMENT_CONFIG_URL_FORMAT);
	}

	public static String contentUploadUrl(String serverUrl, String appId) {
		return createUploadUrl(serverUrl, appId, DEPLOYMENT_CONTENT_URL_FORMAT);
	}

	private static String createUploadUrl(String serverUrl, String appId, String template) {
		StringBuilder sb = new StringBuilder(serverUrl);
		String deployUrl = String.format(template, appId);
		if(!serverUrl.endsWith("/")) {
			sb.append("/");
		}
		sb.append(deployUrl);
		return sb.toString();
	}
}
