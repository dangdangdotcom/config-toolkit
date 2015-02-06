/**
 * Copyright 1999-2014 dangdang.com.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dangdang.config.service.easyzk.support.localoverride;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

/**
 * 本地缓存的加载
 * 
 * @author <a href="mailto:wangyuxuan@dangdang.com">Yuxuan Wang</a>
 *
 */
public class LocalOverrideFileLoader {

	public static Map<String, String> loadLocalProperties(String rootNode, String group) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(rootNode) && !Strings.isNullOrEmpty(group), "rootNode or group cannot be empty.");

		Map<String, String> properties = null;
		final String localOverrideFile = findLocalOverrideFile();
		InputStream in = null;
		try {
			in = LocalOverrideFileLoader.class.getClassLoader().getResourceAsStream(localOverrideFile);
			if (in != null) {
				final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				final DocumentBuilder builder = factory.newDocumentBuilder();

				final Document doc = builder.parse(in);
				final Element factoriesNode = Preconditions.checkNotNull(doc.getDocumentElement(), "Root xml node node-factories not exists.");

				Node factoryNode = findChild(factoriesNode, "node-factory", "root", rootNode);
				if (factoryNode != null) {
					Node nodeGroup = findChild(factoryNode, "group", "id", group);
					if (nodeGroup != null) {
						NodeList childNodes = nodeGroup.getChildNodes();
						int nodeCount = childNodes.getLength();
						if (nodeCount > 0) {
							properties = Maps.newHashMap();
							for (int i = 0; i < nodeCount; i++) {
								Node item = childNodes.item(i);
								if (item.hasAttributes()) {
									NamedNodeMap attributes = item.getAttributes();
									Node keyAttr = attributes.getNamedItem("key");
									if (keyAttr != null) {
										String propKey = keyAttr.getNodeValue();
										String propVal = item.getFirstChild().getNodeValue();
										if (propKey != null && propVal != null) {
											properties.put(propKey, propVal);
										}
									}

								}

							}
						}
					}
				}
			}
		} catch (ParserConfigurationException e) {
			throw Throwables.propagate(e);
		} catch (SAXException e) {
			throw Throwables.propagate(e);
		} catch (IOException e) {
			throw Throwables.propagate(e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// IGNORE
				}
			}
		}
		return properties;
	}

	/**
	 * Find local override file name
	 * 
	 * @return
	 */
	private static String findLocalOverrideFile() {
		String evnKey = System.getenv(DefaultConfigs.LOCAL_OVERRIDE_FILE_EVN_KEY);
		if (evnKey == null) {
			evnKey = DefaultConfigs.DEFAULT_LOCAL_OVERRIDE_FILE;
		}
		return evnKey;
	}

	/**
	 * 查找子结点
	 * 
	 * @param parent
	 *            父节点
	 * @param childNodeName
	 *            子结点名字
	 * @param attrName
	 *            子结点属性名
	 * @param attrValue
	 *            子结点属性值
	 * @return
	 */
	private static Node findChild(Node parent, String childNodeName, String attrName, String attrValue) {
		if (parent.hasChildNodes()) {
			final NodeList childNodes = parent.getChildNodes();
			final int nodeCount = childNodes.getLength();
			for (int i = 0; i < nodeCount; i++) {
				Node item = childNodes.item(i);
				if (item.hasAttributes()) {
					Node attr = item.getAttributes().getNamedItem(attrName);
					if (attr != null && attrValue.equals(attr.getNodeValue())) {
						return item;
					}
				}
			}
		}
		return null;
	}

}
