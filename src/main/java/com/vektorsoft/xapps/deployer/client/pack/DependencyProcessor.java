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

import java.io.File;
import java.nio.file.Path;

/**
 * Processor for configuration @{code dependency} elements. This implementation handles
 * both Maven dependencies and local dependencies.
 *
 * Local dependencies will be searched in path specified in it's configuration element, relative to project root directory.
 * Maven dependencies are searched in local Maven repository. By default, this is the {@code <user_home>/.m2} directory. This path
 * can be changed by setting system property {@code local.maven.repo}, with value being  absolute path to Maven repository.
 */
public class DependencyProcessor implements ConfigElementProcessor {

	private final File contentDir;
	private final File sourceDir;

	/**
	 * Creates new instance of this processor.
	 *
	 * @param targetDir output directory where artifacts will be stores
	 * @param sourceDir source directory (project root directory)
	 */
	public DependencyProcessor(File targetDir, File sourceDir) {
		this.contentDir = targetDir;
		this.sourceDir = sourceDir;
	}

	@Override
	public void process(Element configElement) throws DeployerException {
		String path = configElement.getAttribute("path");
		if(path != null) {
			var processor = new LocalResourceProcessor(contentDir, sourceDir);
			processor.process(configElement);
		} else {

		}
	}

	private void handleManagedDependency(Element configElement) {
		String type = configElement.getAttribute("xsi:type");
		if("mavenDependency".equals(type)) {

		}
	}
}
