/*
 * Copyright (c) 2019. Vladimir Djurovic
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.vektorsoft.xapps.deployer.client.xml;

import com.vektorsoft.xapps.deployer.client.DeployerException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class XmlDataExtractor {

	private final DocumentBuilderFactory documentBuilderFactory;
	private final XPath xpath;
	private final TransformerFactory transformerFactory;

	public XmlDataExtractor() {
		documentBuilderFactory = DocumentBuilderFactory.newInstance();
		xpath = XPathFactory.newInstance().newXPath();
		transformerFactory = TransformerFactory.newInstance();
	}

	public String extractApplicationData(File dataFile) throws DeployerException {
		try {
			Document document =documentBuilderFactory.newDocumentBuilder().parse(dataFile);
			Node node = (Node) xpath.evaluate("project/application", document, XPathConstants.NODE);

			return convertNodeToString(node);
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException | XPathExpressionException ex) {
			throw new DeployerException((ex));
		}

	}

	private String convertNodeToString(Node node) throws TransformerException {
		StringWriter writer = new StringWriter();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.transform(new DOMSource(node), new StreamResult(writer));
		return writer.toString();
	}
}
