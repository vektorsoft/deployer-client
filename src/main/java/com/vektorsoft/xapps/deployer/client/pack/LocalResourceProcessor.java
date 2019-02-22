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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class LocalResourceProcessor implements ConfigElementProcessor {

	private final File contentDir;
	private final File sourceDir;

	public LocalResourceProcessor(File target, File sourceDir) {
		this.contentDir = target;
		this.sourceDir = sourceDir;
	}

	@Override
	public void process(Element configElement) throws DeployerException {
		String localPath = configElement.getAttribute("path");
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

		Path targetPath = Path.of(contentDir.getAbsolutePath(), parts[0], parts[1], parts[2], hash);
		targetPath.toFile().mkdirs();
		try {
			Files.copy(source.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
		} catch(IOException ex) {
			throw new DeployerException(ex);
		}

	}

}
