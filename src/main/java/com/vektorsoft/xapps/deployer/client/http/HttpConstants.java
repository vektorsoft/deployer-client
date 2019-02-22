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

	/**
	 * Private constructor to prevent instantiation.
	 */
	private HttpConstants() {

	}

	public static String configUploadUrl(String serverUrl, String appId) {
		StringBuilder sb = new StringBuilder(serverUrl);
		String deployUrl = String.format(DEPLOYMENT_CONFIG_URL_FORMAT, appId);
		if(!serverUrl.endsWith("/")) {
			sb.append("/");
		}
		sb.append(deployUrl);
		return sb.toString();
	}
}
