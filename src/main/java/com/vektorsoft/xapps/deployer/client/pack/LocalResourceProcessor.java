/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.pack;

import com.vektorsoft.xapps.deployer.client.DeployerException;
import com.vektorsoft.xapps.deployer.client.HashCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Processor for local deployment elements, such as local dependencies or resources.
 */
public class LocalResourceProcessor implements ConfigElementProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(LocalResourceProcessor.class);

	private final File contentDir;
	private final File sourceDir;

	public LocalResourceProcessor(File target, File sourceDir) {
		this.contentDir = target;
		this.sourceDir = sourceDir;
	}

	@Override
	public void process(Element configElement) throws DeployerException {
		LOGGER.info("Processing element {}", configElement.getTagName());
		String localPath = configElement.getAttribute("path");
		LOGGER.debug("Found local path {}", localPath);
		if(localPath == null) {
			throw  new DeployerException("Missing 'path' attribute");
		}
		File source = new File(sourceDir, localPath);
		String hash = HashCalculator.fileHash(source);
		String[] parts = new String[]{
				hash.substring(0, 2),
				hash.substring(2, 4),
				hash.substring(4, 6)
		};
		LOGGER.debug("Calculated file hash as {}", hash);

		Path targetPath = Path.of(contentDir.getAbsolutePath(), parts[0], parts[1], parts[2], hash);
		targetPath.toFile().mkdirs();
		try {
			Files.copy(source.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.debug("Copied local resource file to {}", targetPath.toString());
		} catch(IOException ex) {
			throw new DeployerException(ex);
		}

	}

}
