/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.pack;

import com.vektorsoft.xapps.deployer.client.DeployerException;
import org.w3c.dom.Element;

/**
 * Defines methods related to processing of configuration nodes.
 */
public interface ConfigElementProcessor {

	/**
	 * Performs processing of single XML configuration node.
	 *
	 * @param configElement XML element
	 * @throws DeployerException if an error occurs
	 */
	void process(Element configElement) throws DeployerException;
}
