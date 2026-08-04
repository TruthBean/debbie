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

import java.util.Objects;

/**
 * Represents an XML document with an optional XML declaration and a root element.
 * <p>
 * A document has a single root node and optional declaration attributes
 * such as version and encoding.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class XmlDocument {

    private XmlNode root;
    private String version;
    private String encoding;
    private Boolean standalone;

    /**
     * Creates an empty document. You must set the root node before use.
     */
    public XmlDocument() {
    }

    /**
     * Creates a document with the given root element.
     *
     * @param root the root element
     */
    public XmlDocument(XmlNode root) {
        setRoot(root);
    }

    /**
     * Creates a document with the given root element name.
     *
     * @param rootName the tag name for the root element
     */
    public XmlDocument(String rootName) {
        this.root = new XmlNode(rootName);
    }

    // ============ root ============

    /**
     * @return the root element
     */
    public XmlNode getRoot() {
        return root;
    }

    /**
     * Sets the root element.
     *
     * @param root the root element
     */
    public void setRoot(XmlNode root) {
        this.root = Objects.requireNonNull(root, "root element must not be null");
    }

    // ============ declaration ============

    /**
     * @return the XML version, or null if not set
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the XML version (e.g. "1.0")
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the encoding, or null if not set
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding (e.g. "UTF-8")
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * @return the standalone flag, or null if not set
     */
    public Boolean getStandalone() {
        return standalone;
    }

    /**
     * @param standalone the standalone flag
     */
    public void setStandalone(Boolean standalone) {
        this.standalone = standalone;
    }

    /**
     * @return true if this document has an XML declaration
     */
    public boolean hasDeclaration() {
        return version != null || encoding != null || standalone != null;
    }

    // ============ convenience ============

    /**
     * @return the root element's tag name
     */
    public String getRootName() {
        return root != null ? root.getName() : null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlDocument that = (XmlDocument) o;
        return Objects.equals(root, that.root)
                && Objects.equals(version, that.version)
                && Objects.equals(encoding, that.encoding)
                && Objects.equals(standalone, that.standalone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root, version, encoding, standalone);
    }

    @Override
    public String toString() {
        return "XmlDocument{" +
                "root=" + (root != null ? root.getName() : "null") +
                ", version='" + version + '\'' +
                ", encoding='" + encoding + '\'' +
                '}';
    }
}