/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.yaml;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;

/**
 * Serializes a {@link YamlDocument} or {@link YamlNode} tree into a YAML string.
 * <p>
 * Supports compact and pretty-print output modes. Uses block-style output
 * for most structures, with flow-style for inline values.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class YamlSerializer {

    private final boolean prettyPrint;
    private final String indent;

    /**
     * Creates a serializer with compact output (no extra whitespace).
     */
    public YamlSerializer() {
        this(false);
    }

    /**
     * @param prettyPrint if true, output is formatted with indentation
     */
    public YamlSerializer(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
        this.indent = "  ";
    }

    /**
     * @param prettyPrint if true, output is formatted
     * @param indent      the indent string (e.g. "  " or "\t")
     */
    public YamlSerializer(boolean prettyPrint, String indent) {
        this.prettyPrint = prettyPrint;
        this.indent = indent;
    }

    /**
     * Serializes a {@link YamlDocument} to a YAML string.
     *
     * @param document the document to serialize
     * @return the YAML string
     */
    public String serialize(YamlDocument document) {
        StringBuilder sb = new StringBuilder();
        if (document.isSingleDocument()) {
            writeNode(document.getRoot(), sb, 0);
        } else {
            for (int i = 0; i < document.documentCount(); i++) {
                if (i > 0) {
                    sb.append("---");
                    if (prettyPrint) {
                        sb.append('\n');
                    }
                }
                writeNode(document.getDocument(i), sb, 0);
                if (prettyPrint && i < document.documentCount() - 1) {
                    sb.append('\n');
                }
            }
        }
        if (prettyPrint && sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
            sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Serializes a {@link YamlNode} to a YAML string.
     *
     * @param node the node to serialize
     * @return the YAML string
     */
    public String serialize(YamlNode node) {
        StringBuilder sb = new StringBuilder();
        writeNode(node, sb, 0);
        return sb.toString();
    }

    // ============ write ============

    private void writeNode(YamlNode node, StringBuilder sb, int depth) {
        if (node == null || node.isNull()) {
            sb.append("null");
        } else if (node.isMapping()) {
            writeMapping(node.asMapping(), sb, depth);
        } else if (node.isSequence()) {
            writeSequence(node.asSequence(), sb, depth);
        } else if (node.isScalar()) {
            writeScalar(node.asScalar(), sb);
        }
    }

    // ============ mapping ============

    private void writeMapping(YamlMapping mapping, StringBuilder sb, int depth) {
        if (mapping.isEmpty()) {
            sb.append("{}");
            return;
        }
        boolean first = true;
        for (Map.Entry<String, YamlNode> entry : mapping) {
            if (!first) {
                if (prettyPrint) {
                    sb.append('\n');
                }
            }
            first = false;
            if (prettyPrint) {
                indent(sb, depth);
            }
            writeKey(entry.getKey(), sb);
            sb.append(':');
            YamlNode value = entry.getValue();
            if (value == null || value.isNull()) {
                if (prettyPrint) {
                    sb.append(' ');
                }
                sb.append("null");
            } else if (value.isScalar() && !value.asScalar().isString()) {
                if (prettyPrint) {
                    sb.append(' ');
                }
                sb.append(' ');
                writeScalarInline(value.asScalar(), sb);
            } else if (value.isScalar()) {
                String str = value.asScalar().getAsString();
                if (str.isEmpty()) {
                    if (prettyPrint) {
                        sb.append(' ');
                    }
                    sb.append("''");
                } else if (needsQuoting(str)) {
                    if (prettyPrint) {
                        sb.append(' ');
                    }
                    sb.append(' ');
                    sb.append(quoteString(str));
                } else {
                    if (prettyPrint) {
                        sb.append(' ');
                    }
                    sb.append(' ');
                    sb.append(str);
                }
            } else if (value.isSequence()) {
                YamlSequence seq = value.asSequence();
                if (seq.isEmpty()) {
                    if (prettyPrint) {
                        sb.append(' ');
                    }
                    sb.append("[]");
                } else {
                    if (prettyPrint) {
                        sb.append('\n');
                    }
                    writeSequence(value.asSequence(), sb, depth + 1);
                }
            } else if (value.isMapping()) {
                YamlMapping map = value.asMapping();
                if (map.isEmpty()) {
                    if (prettyPrint) {
                        sb.append(' ');
                    }
                    sb.append("{}");
                } else {
                    if (prettyPrint) {
                        sb.append('\n');
                    }
                    writeMapping(value.asMapping(), sb, depth + 1);
                }
            }
        }
    }

    // ============ sequence ============

    private void writeSequence(YamlSequence sequence, StringBuilder sb, int depth) {
        if (sequence.isEmpty()) {
            sb.append("[]");
            return;
        }
        for (YamlNode element : sequence) {
            if (prettyPrint) {
                indent(sb, depth);
            }
            sb.append("- ");
            if (element == null || element.isNull()) {
                sb.append("null");
            } else if (element.isScalar()) {
                String str = element.asScalar().getAsString();
                if (needsQuoting(str)) {
                    sb.append(quoteString(str));
                } else {
                    sb.append(str);
                }
            } else if (element.isMapping()) {
                YamlMapping map = element.asMapping();
                if (map.isEmpty()) {
                    sb.append("{}");
                } else {
                    if (prettyPrint) {
                        sb.append('\n');
                    }
                    writeMapping(map, sb, depth + 1);
                }
            } else if (element.isSequence()) {
                YamlSequence seq = element.asSequence();
                if (seq.isEmpty()) {
                    sb.append("[]");
                } else {
                    if (prettyPrint) {
                        sb.append('\n');
                    }
                    writeSequence(seq, sb, depth + 1);
                }
            }
            if (prettyPrint) {
                sb.append('\n');
            }
        }
    }

    // ============ scalar ============

    private void writeScalar(YamlScalar scalar, StringBuilder sb) {
        if (scalar.isString()) {
            String str = scalar.getAsString();
            if (needsQuoting(str)) {
                sb.append(quoteString(str));
            } else {
                sb.append(str);
            }
        } else if (scalar.isBoolean()) {
            sb.append(scalar.getAsBoolean() ? "true" : "false");
        } else if (scalar.isNumber()) {
            Number num = scalar.getAsNumber();
            if (num instanceof BigDecimal) {
                sb.append(((BigDecimal) num).toPlainString());
            } else {
                sb.append(num.toString());
            }
        }
    }

    private void writeScalarInline(YamlScalar scalar, StringBuilder sb) {
        if (scalar.isBoolean()) {
            sb.append(scalar.getAsBoolean() ? "true" : "false");
        } else if (scalar.isNumber()) {
            Number num = scalar.getAsNumber();
            if (num instanceof BigDecimal) {
                sb.append(((BigDecimal) num).toPlainString());
            } else {
                sb.append(num.toString());
            }
        } else {
            writeScalar(scalar, sb);
        }
    }

    // ============ key writing ============

    private void writeKey(String key, StringBuilder sb) {
        if (needsQuoting(key)) {
            sb.append(quoteString(key));
        } else {
            sb.append(key);
        }
    }

    // ============ quoting helpers ============

    private boolean needsQuoting(String str) {
        if (str.isEmpty()) return true;
        // Check for special YAML characters
        if (str.charAt(0) == ' ' || str.charAt(str.length() - 1) == ' ') return true;
        if (str.startsWith("#") || str.startsWith("&") || str.startsWith("*") ||
                str.startsWith("!") || str.startsWith("|") || str.startsWith(">") ||
                str.startsWith("'") || str.startsWith("\"")) return true;
        if (str.startsWith("{") || str.startsWith("[") || str.startsWith("?") || str.startsWith("-")) return true;
        if (str.contains(": ") || str.contains(" #")) return true;
        // Check for boolean/null keywords
        if (str.equals("true") || str.equals("True") || str.equals("TRUE") ||
                str.equals("false") || str.equals("False") || str.equals("FALSE") ||
                str.equals("yes") || str.equals("Yes") || str.equals("YES") ||
                str.equals("no") || str.equals("No") || str.equals("NO") ||
                str.equals("on") || str.equals("On") || str.equals("ON") ||
                str.equals("off") || str.equals("Off") || str.equals("OFF") ||
                str.equals("null") || str.equals("Null") || str.equals("NULL") ||
                str.equals("~")) return true;
        // Check number
        try {
            Double.parseDouble(str);
            return true; // quote numbers to preserve them as strings
        } catch (NumberFormatException e) {
            // not a number
        }
        return false;
    }

    private String quoteString(String str) {
        // Use single quotes by default, double quotes if string contains single quotes
        if (str.contains("'")) {
            return "\"" + escapeDoubleQuoted(str) + "\"";
        }
        return "'" + str + "'";
    }

    private String escapeDoubleQuoted(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"' : sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    private void indent(StringBuilder sb, int depth) {
        for (int i = 0; i < depth; i++) {
            sb.append(indent);
        }
    }
}