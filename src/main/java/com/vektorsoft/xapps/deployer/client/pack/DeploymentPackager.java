/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.pack;

import com.vektorsoft.xapps.deployer.client.DeployerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.zeroturnaround.zip.ZipUtil;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

/**
 * This class is responsible for packaging deployment content into zip archive to be uploaded
 * to server. Created archive has predefined structure, so it can be processed on server.
 */
public class DeploymentPackager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentPackager.class);

	private static final String DEFAULT_CONFIG_FILE_NAME = "deployer-config.xml";
	private static final String DEFAULT_DIFF_FILE_NAME = "diff.xml";

	private final File deploymentDir;
	private final File contentDir;
	private final File projectDir;

	/**
	 * Creates new packager instance.
	 *
	 * @param applicationId application ID
	 * @param projectDir project root directory
	 */
	public DeploymentPackager(String applicationId, File projectDir) {
		this.projectDir = projectDir;
		String tmpDirPath = System.getProperty("java.io.tmpdir");
		deploymentDir = new File(tmpDirPath, applicationId);
		contentDir = new File(deploymentDir, "content");
		contentDir.mkdirs();
	}

	/**
	 * Create deployment package based on specified configuration.
	 * @param configDiff configuration difference file
	 * @return create deployment archive file
	 * @throws DeployerException if an error occurs
	 */
	public File pack(String configDiff) throws DeployerException {
		LOGGER.info("Started packaging deployment package");
		writeDiffFile(deploymentDir, configDiff);
		LOGGER.info("Wrote diff file to {}", deploymentDir.getAbsolutePath());
		writeConfigFile(deploymentDir);
		writeContent(configDiff);

		File archiveFile = new File(deploymentDir.getParent(), deploymentDir.getName() + ".zip");
		compressDeploymentDirectory(archiveFile);
		LOGGER.info("Deployment package created at {}", archiveFile.getAbsolutePath());
		deleteTemporaryDirectory();
		return archiveFile;
	}

	private void compressDeploymentDirectory(File archiveFile) {
		ZipUtil.pack(deploymentDir, archiveFile);
	}



	private void writeDiffFile(File deployDir, String content) throws  DeployerException {
		try (PrintWriter writer = new PrintWriter(new FileWriter(new File(deployDir, DEFAULT_DIFF_FILE_NAME)))) {
			writer.print(content);
		} catch(IOException ex) {
			throw new DeployerException("Failed to create diff file", ex);
		}
	}

	private void writeConfigFile(File deployDir) throws DeployerException {
		var configFilePath = Path.of(projectDir.getAbsolutePath(), DEFAULT_CONFIG_FILE_NAME);
		var targetPath = Path.of(deployDir.getAbsolutePath(), DEFAULT_CONFIG_FILE_NAME);
		try {
			Files.copy(configFilePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("Configuration file copied successfully");
		} catch(IOException ex) {
			LOGGER.error("Failed to copy configuration file", ex);
		}

	}

	private void writeContent(String config) throws DeployerException {
		Document doc = createXmlDocument(config);
		iterateConfigNodes(doc, "icon", new LocalResourceProcessor(contentDir, projectDir));
		iterateConfigNodes(doc, "splash-screen", new LocalResourceProcessor(contentDir, projectDir));
		iterateConfigNodes(doc, "dependency", new DependencyProcessor(contentDir, projectDir));
	}

	private Document createXmlDocument(String configData) throws DeployerException {
		try {
			var dbFactory = DocumentBuilderFactory.newInstance();
			var docBuilder = dbFactory.newDocumentBuilder();
			return docBuilder.parse(new InputSource(new StringReader(configData)));
		} catch(ParserConfigurationException | SAXException | IOException ex) {
			throw new DeployerException("Could not parse configuration", ex);
		}

	}

	private void iterateConfigNodes(Document document, String tagName, ConfigElementProcessor processor) throws DeployerException {
		NodeList tagNodes = document.getElementsByTagName(tagName);
		for(int i = 0;i < tagNodes.getLength();i++) {
			Node node = tagNodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				processor.process(element);
			}
		}
	}

	private void deleteTemporaryDirectory() {
		Path pathToDelete = deploymentDir.toPath();
		try {
			Files.walk(pathToDelete)
					.sorted(Comparator.reverseOrder())
					.map(Path::toFile)
					.forEach(File::delete);
			LOGGER.info("Successfully deleted temporary deployment directory");
		} catch(IOException ex) {
			LOGGER.error("Failed to delete temporary deployment directory", ex);
		}

	}
}
