/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.xml;

import java.io.*;

/**
 * Convenience utility class for XML serialization and deserialization.
 * <p>
 * All methods are pure Java with no third-party dependencies.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class XmlUtils {

    private XmlUtils() {
    }

    // ============ parse ============

    /**
     * Parses an XML string into an {@link XmlDocument}.
     *
     * @param xml the XML string
     * @return the parsed document
     * @throws XmlException if the XML is not well-formed
     */
    public static XmlDocument parse(String xml) {
        return XmlParser.parse(xml);
    }

    /**
     * Parses an XML string from an {@link InputStream}.
     *
     * @param inputStream the input stream
     * @return the parsed document
     * @throws XmlException if the XML is not well-formed or I/O error occurs
     */
    public static XmlDocument parse(InputStream inputStream) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[8192];
            int n;
            while ((n = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, n);
            }
            buffer.flush();
            return XmlParser.parse(buffer.toString("UTF-8"));
        } catch (IOException e) {
            throw new XmlException("Failed to read XML from input stream", e);
        }
    }

    // ============ serialize ============

    /**
     * Serializes an {@link XmlDocument} to a compact XML string.
     *
     * @param doc the document to serialize
     * @return the XML string
     */
    public static String serialize(XmlDocument doc) {
        return new XmlSerializer(false).serialize(doc);
    }

    /**
     * Serializes an {@link XmlDocument} to a pretty-printed XML string.
     *
     * @param doc the document to serialize
     * @return the formatted XML string
     */
    public static String prettyPrint(XmlDocument doc) {
        return new XmlSerializer(true).serialize(doc);
    }

    /**
     * Serializes an {@link XmlNode} to a compact XML string.
     *
     * @param node the node to serialize
     * @return the XML string
     */
    public static String serialize(XmlNode node) {
        return new XmlSerializer(false).serialize(node);
    }

    // ============ node construction helpers ============

    /**
     * Creates a new XML element with the given tag name.
     *
     * @param name the tag name
     * @return a new XmlNode
     */
    public static XmlNode element(String name) {
        return new XmlNode(name);
    }

    /**
     * Creates a new XML element with text content.
     *
     * @param name    the tag name
     * @param text    the text content
     * @return a new XmlNode with text content
     */
    public static XmlNode element(String name, String text) {
        XmlNode node = new XmlNode(name);
        node.setTextContent(text);
        return node;
    }

    /**
     * Creates a new XML element with children.
     *
     * @param name     the tag name
     * @param children the child nodes
     * @return a new XmlNode with children
     */
    public static XmlNode element(String name, XmlNode... children) {
        XmlNode node = new XmlNode(name);
        for (XmlNode child : children) {
            node.appendChild(child);
        }
        return node;
    }
}