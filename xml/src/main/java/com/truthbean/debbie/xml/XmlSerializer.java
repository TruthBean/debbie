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

import java.util.Map;

/**
 * Serializes an {@link XmlDocument} to an XML string.
 * <p>
 * Supports compact and pretty-print output modes. In pretty-print mode,
 * elements are indented to show the document's hierarchical structure.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class XmlSerializer {

    private final boolean prettyPrint;
    private final String indent;

    /**
     * Creates a serializer with compact output (no indentation).
     */
    public XmlSerializer() {
        this(false);
    }

    /**
     * @param prettyPrint if true, output is formatted with indentation
     */
    public XmlSerializer(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.indent = "  ";
    }

    /**
     * @param prettyPrint if true, output is formatted
     * @param indent      the indent string (e.g. "  " or "\t")
     */
    public XmlSerializer(boolean prettyPrint, String indent) {
        this.prettyPrint = prettyPrint;
        this.indent = indent;
    }

    /**
     * Serializes an {@link XmlDocument} to an XML string.
     *
     * @param doc the document to serialize
     * @return the XML string
     */
    public String serialize(XmlDocument doc) {
        StringBuilder sb = new StringBuilder();
        if (doc.hasDeclaration()) {
            sb.append("<?xml");
            if (doc.getVersion() != null) {
                sb.append(" version=\"").append(escapeAttribute(doc.getVersion())).append('"');
            }
            if (doc.getEncoding() != null) {
                sb.append(" encoding=\"").append(escapeAttribute(doc.getEncoding())).append('"');
            }
            if (doc.getStandalone() != null) {
                sb.append(" standalone=\"").append(doc.getStandalone() ? "yes" : "no").append('"');
            }
            sb.append("?>");
            if (prettyPrint) {
                sb.append('\n');
            }
        }
        if (doc.getRoot() != null) {
            writeNode(doc.getRoot(), sb, 0);
        }
        if (prettyPrint) {
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Serializes an {@link XmlNode} to an XML string.
     *
     * @param node the node to serialize
     * @return the XML string
     */
    public String serialize(XmlNode node) {
        StringBuilder sb = new StringBuilder();
        writeNode(node, sb, 0);
        return sb.toString();
    }

    // ============ node ============

    private void writeNode(XmlNode node, StringBuilder sb, int depth) {
        if (prettyPrint) {
            indent(sb, depth);
        }

        sb.append('<').append(node.getName());

        // Attributes
        for (Map.Entry<String, String> attr : node.getAttributes().entrySet()) {
            sb.append(' ')
                    .append(attr.getKey())
                    .append('=')
                    .append('"')
                    .append(escapeAttribute(attr.getValue()))
                    .append('"');
        }

        // Content
        if (node.isLeaf()) {
            sb.append("/>");
            if (prettyPrint) {
                sb.append('\n');
            }
        } else if (node.isTextOnly()) {
            sb.append('>');
            sb.append(escapeText(node.getTextContent()));
            sb.append("</").append(node.getName()).append('>');
            if (prettyPrint) {
                sb.append('\n');
            }
        } else {
            sb.append('>');
            if (prettyPrint) {
                sb.append('\n');
            }
            // Children
            for (XmlNode child : node.getChildren()) {
                writeNode(child, sb, depth + 1);
            }
            if (prettyPrint) {
                indent(sb, depth);
            }
            sb.append("</").append(node.getName()).append('>');
            if (prettyPrint) {
                sb.append('\n');
            }
        }
    }

    // ============ escaping ============

    private String escapeAttribute(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '"' -> sb.append("&quot;");
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private String escapeText(String value) {
        StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    // ============ indent ============

    private void indent(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append(indent);
        }
    }
}