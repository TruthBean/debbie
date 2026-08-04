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
 * Recursive descent XML parser.
 * <p>
 * Parses an XML string into an {@link XmlDocument} with a tree of {@link XmlNode} elements.
 * Supports:
 * <ul>
 *   <li>XML declaration ({@code <?xml version="1.0" encoding="UTF-8"?>})</li>
 *   <li>Elements with attributes</li>
 *   <li>Self-closing tags ({@code <tag/>})</li>
 *   <li>Text content and nested elements</li>
 *   <li>CDATA sections ({@code <![CDATA[...]]>})</li>
 *   <li>Comments ({@code <!-- ... -->})</li>
 *   <li>Entity references: {@code &amp; &lt; &gt; &quot; &apos;}</li>
 *   <li>Numeric character references: {@code &#NN;}</li>
 * </ul>
 * <p>
 * This parser is lenient with well-formedness but will reject obvious errors
 * such as mismatched tags or malformed syntax.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class XmlParser {

    private final String xml;
    private int pos;

    private XmlParser(String xml) {
        this.xml = Objects.requireNonNull(xml, "xml must not be null");
        this.pos = 0;
    }

    /**
     * Parses an XML string into an {@link XmlDocument}.
     *
     * @param xml the XML string to parse
     * @return the parsed document
     * @throws XmlException if the XML is not well-formed
     */
    public static XmlDocument parse(String xml) {
        XmlParser parser = new XmlParser(xml);
        XmlDocument doc = new XmlDocument();
        parser.skipWhitespace();

        // XML declaration
        if (parser.startsWith("<?xml")) {
            parser.parseDeclaration(doc);
            parser.skipWhitespace();
        }

        // Root element
        if (parser.pos >= parser.xml.length()) {
            throw new XmlException("Unexpected end of XML: expected root element");
        }
        XmlNode root = parser.parseElement();
        doc.setRoot(root);

        parser.skipWhitespace();
        if (parser.pos < parser.xml.length()) {
            throw new XmlException("Unexpected content after root element at position " + parser.pos);
        }

        return doc;
    }

    // ============ declaration ============

    private void parseDeclaration(XmlDocument doc) {
        // <?xml version="1.0" encoding="UTF-8"?>
        expect("<?xml");
        skipWhitespace();

        while (pos < xml.length()) {
            skipWhitespace();
            if (startsWith("?>")) {
                pos += 2;
                return;
            }
            String attrName = parseName();
            if (attrName.isEmpty()) {
                throw new XmlException("Expected attribute name in XML declaration at position " + pos);
            }
            skipWhitespace();
            expect('=');
            skipWhitespace();
            String attrValue = parseAttributeValue();

            switch (attrName) {
                case "version" -> doc.setVersion(attrValue);
                case "encoding" -> doc.setEncoding(attrValue);
                case "standalone" -> doc.setStandalone("yes".equals(attrValue));
                default -> throw new XmlException("Unknown XML declaration attribute: " + attrName + " at position " + (pos - attrName.length()));
            }
            skipWhitespace();
        }
        throw new XmlException("Unterminated XML declaration");
    }

    // ============ element ============

    private XmlNode parseElement() {
        expect('<');
        // check for comment
        if (startsWith("!--")) {
            skipComment();
            skipWhitespace();
            return parseElement();
        }
        // check for CDATA - should not appear at element level
        if (startsWith("![CDATA[")) {
            throw new XmlException("CDATA section not allowed as element at position " + pos);
        }

        String tagName = parseName();
        if (tagName.isEmpty()) {
            throw new XmlException("Expected tag name at position " + pos);
        }

        XmlNode node = new XmlNode(tagName);

        // Parse attributes
        skipWhitespace();
        while (pos < xml.length() && !startsWith("/>") && !startsWith(">") && !startsWith("?>")) {
            String attrName = parseName();
            if (attrName.isEmpty()) {
                break;
            }
            skipWhitespace();
            if (pos < xml.length() && xml.charAt(pos) == '=') {
                pos++;
                skipWhitespace();
                String attrValue = parseAttributeValue();
                node.setAttribute(attrName, attrValue);
            } else {
                // boolean attribute (no value, e.g. <option disabled>)
                node.setAttribute(attrName, attrName);
            }
            skipWhitespace();
        }

        // Self-closing tag
        if (startsWith("/>")) {
            pos += 2;
            return node;
        }

        // End of opening tag
        expect('>');

        // Parse content
        parseContent(node);

        // Closing tag
        parseClosingTag(tagName);

        return node;
    }

    private void parseContent(XmlNode parent) {
        StringBuilder text = new StringBuilder();
        while (pos < xml.length()) {
            skipWhitespaceInText(text);

            if (pos >= xml.length()) {
                break;
            }

            if (xml.charAt(pos) == '<') {
                // Check for comment or CDATA
                if (startsWith("<!--")) {
                    // Flush accumulated text
                    if (!text.isEmpty()) {
                        parent.setTextContent(text.toString().trim());
                        text.setLength(0);
                    }
                    pos++; // consume '<', skipComment expects "!--"
                    skipComment();
                    continue;
                }
                if (startsWith("<![CDATA[")) {
                    // Flush accumulated text
                    if (!text.isEmpty()) {
                        parent.setTextContent(text.toString().trim());
                        text.setLength(0);
                    }
                    String cdata = parseCDATA();
                    parent.setTextContent(cdata);
                    continue;
                }
                if (startsWith("</")) {
                    // End tag: flush text and stop
                    if (!text.isEmpty()) {
                        String trimmed = text.toString().trim();
                        if (!trimmed.isEmpty()) {
                            parent.setTextContent(trimmed);
                        }
                        text.setLength(0);
                    }
                    return;
                }
                // Child element
                if (!text.isEmpty()) {
                    String trimmed = text.toString().trim();
                    if (!trimmed.isEmpty()) {
                        parent.setTextContent(trimmed);
                    }
                    text.setLength(0);
                }
                XmlNode child = parseElement();
                parent.appendChild(child);
            } else {
                text.append(xml.charAt(pos));
                pos++;
            }
        }
        // Flush remaining text
        if (!text.isEmpty()) {
            String trimmed = text.toString().trim();
            if (!trimmed.isEmpty()) {
                parent.setTextContent(trimmed);
            }
        }
    }

    private void skipWhitespaceInText(StringBuilder text) {
        while (pos < xml.length() && isWhitespace(xml.charAt(pos))) {
            text.append(xml.charAt(pos));
            pos++;
        }
    }

    private void parseClosingTag(String tagName) {
        expect("</");
        String closeName = parseName();
        skipWhitespace();
        expect('>');
        if (!closeName.equals(tagName)) {
            throw new XmlException("Mismatched closing tag: expected </" + tagName + "> but found </" + closeName + "> at position " + (pos - closeName.length() - 2));
        }
    }

    // ============ attributes ============

    private String parseAttributeValue() {
        if (pos >= xml.length()) {
            throw new XmlException("Unexpected end of XML in attribute value");
        }
        char quote = xml.charAt(pos);
        if (quote != '"' && quote != '\'') {
            throw new XmlException("Expected attribute value quote at position " + pos + ", found '" + quote + "'");
        }
        pos++;
        StringBuilder sb = new StringBuilder();
        while (pos < xml.length()) {
            char c = xml.charAt(pos);
            if (c == quote) {
                pos++;
                return sb.toString();
            }
            if (c == '&') {
                sb.append(parseEntityReference());
            } else {
                sb.append(c);
                pos++;
            }
        }
        throw new XmlException("Unterminated attribute value");
    }

    // ============ entity references ============

    private String parseEntityReference() {
        StringBuilder sb = new StringBuilder();
        sb.append('&');
        pos++;
        while (pos < xml.length()) {
            char c = xml.charAt(pos);
            if (c == ';') {
                sb.append(';');
                pos++;
                String ref = sb.toString();
                return switch (ref) {
                    case "&amp;" -> "&";
                    case "&lt;" -> "<";
                    case "&gt;" -> ">";
                    case "&quot;" -> "\"";
                    case "&apos;" -> "'";
                    default -> {
                        // Numeric character reference
                        if (ref.startsWith("&#x") || ref.startsWith("&#X")) {
                            try {
                                int codePoint = Integer.parseInt(ref.substring(3, ref.length() - 1), 16);
                                yield String.valueOf((char) codePoint);
                            } catch (NumberFormatException e) {
                                yield ref;
                            }
                        } else if (ref.startsWith("&#")) {
                            try {
                                int codePoint = Integer.parseInt(ref.substring(2, ref.length() - 1));
                                yield String.valueOf((char) codePoint);
                            } catch (NumberFormatException e) {
                                yield ref;
                            }
                        }
                        yield ref;
                    }
                };
            }
            sb.append(c);
            pos++;
        }
        throw new XmlException("Unterminated entity reference");
    }

    // ============ CDATA ============

    private String parseCDATA() {
        expect("<![CDATA[");
        int end = xml.indexOf("]]>", pos);
        if (end < 0) {
            throw new XmlException("Unterminated CDATA section");
        }
        String content = xml.substring(pos, end);
        pos = end + 3; // skip "]]>"
        return content;
    }

    // ============ comments ============

    private void skipComment() {
        // The '<' was already consumed by parseElement, so expect "!--" not "<!--"
        expect("!--");
        int end = xml.indexOf("-->", pos);
        if (end < 0) {
            throw new XmlException("Unterminated comment");
        }
        pos = end + 3; // skip "-->"
    }

    // ============ name parsing ============

    private String parseName() {
        if (pos >= xml.length()) {
            return "";
        }
        char c = xml.charAt(pos);
        if (!isNameStartChar(c)) {
            return "";
        }
        int start = pos;
        pos++;
        while (pos < xml.length() && isNameChar(xml.charAt(pos))) {
            pos++;
        }
        return xml.substring(start, pos);
    }

    private boolean isNameStartChar(char c) {
        return Character.isLetter(c) || c == '_' || c == ':';
    }

    private boolean isNameChar(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == ':' || c == '-' || c == '.';
    }

    // ============ helpers ============

    private boolean startsWith(String s) {
        return xml.startsWith(s, pos);
    }

    private void expect(char c) {
        if (pos >= xml.length() || xml.charAt(pos) != c) {
            throw new XmlException("Expected '" + c + "' at position " + pos
                    + " but found '" + (pos < xml.length() ? xml.charAt(pos) : "EOF") + "'");
        }
        pos++;
    }

    private void expect(String s) {
        if (!startsWith(s)) {
            String found = pos < xml.length() ? xml.substring(pos, Math.min(pos + s.length(), xml.length())) : "EOF";
            throw new XmlException("Expected '" + s + "' at position " + pos + " but found '" + found + "'");
        }
        pos += s.length();
    }

    private void skipWhitespace() {
        while (pos < xml.length() && isWhitespace(xml.charAt(pos))) {
            pos++;
        }
    }

    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r';
    }
}