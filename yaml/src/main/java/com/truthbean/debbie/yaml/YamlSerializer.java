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
import java.util.Map;

/**
 * Serializes a {@link YamlDocument} or {@link YamlNode} tree into a YAML string.
 * <p>
 * Two output modes are supported:
 * <ul>
 *   <li><b>Pretty-print</b> ({@code prettyPrint=true}): block style with indentation.
 *       Nested mappings and sequences are rendered across multiple lines with
 *       increasing indentation.</li>
 *   <li><b>Compact</b> ({@code prettyPrint=false}): top-level entries use block style
 *       (one key per line, required for valid YAML), but nested collections use
 *       flow style ({@code {k: v}} / {@code [a, b]}) to minimize vertical space.</li>
 * </ul>
 * <p>
 * Both modes always produce valid YAML.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class YamlSerializer {

    private final boolean prettyPrint;
    private final String indent;

    /**
     * Creates a serializer with compact output.
     */
    public YamlSerializer() {
        this(false);
    }

    /**
     * @param prettyPrint if true, output is formatted with indentation (block style);
     *                    if false, nested collections use flow style
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
                    // Ensure previous document ends with newline before separator
                    if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
                        sb.append('\n');
                    }
                    sb.append("---\n");
                }
                writeNode(document.getDocument(i), sb, 0);
            }
        }
        // Ensure trailing newline for a well-formed document
        if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '\n') {
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

    // ============ top-level dispatch ============

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

    // ============ block mapping ============

    /**
     * Writes a mapping in block style. Each entry occupies its own line.
     * This is used at the top level in both modes, and for nested mappings
     * in pretty-print mode.
     */
    private void writeMapping(YamlMapping mapping, StringBuilder sb, int depth) {
        if (mapping.isEmpty()) {
            sb.append("{}");
            return;
        }
        boolean first = true;
        for (Map.Entry<String, YamlNode> entry : mapping) {
            if (!first) {
                sb.append('\n');
            }
            first = false;
            indent(sb, depth);
            writeKey(entry.getKey(), sb);
            sb.append(": ");
            writeValueAfterColon(entry.getValue(), sb, depth);
        }
    }

    // ============ block sequence ============

    /**
     * Writes a sequence in block style. Each item occupies its own line
     * starting with {@code - }.
     * This is used at the top level in both modes, and for nested sequences
     * in pretty-print mode.
     */
    private void writeSequence(YamlSequence sequence, StringBuilder sb, int depth) {
        if (sequence.isEmpty()) {
            sb.append("[]");
            return;
        }
        boolean first = true;
        for (YamlNode element : sequence) {
            if (!first) {
                sb.append('\n');
            }
            first = false;
            indent(sb, depth);
            sb.append("- ");
            writeValueAsSequenceItem(element, sb, depth);
        }
    }

    // ============ value writing (after colon in a mapping) ============

    /**
     * Writes a value that follows {@code key: } in a block mapping.
     * <p>
     * Scalars and empty collections are written inline on the same line.
     * Non-empty collections are written on subsequent lines:
     * <ul>
     *   <li>Pretty-print: block style with indentation</li>
     *   <li>Compact: flow style on the same line</li>
     * </ul>
     */
    private void writeValueAfterColon(YamlNode value, StringBuilder sb, int depth) {
        if (value == null || value.isNull()) {
            sb.append("null");
        } else if (value.isScalar()) {
            writeScalar(value.asScalar(), sb);
        } else if (value.isMapping()) {
            YamlMapping map = value.asMapping();
            if (map.isEmpty()) {
                sb.append("{}");
            } else if (prettyPrint) {
                sb.append('\n');
                writeMapping(map, sb, depth + 1);
            } else {
                writeFlowMapping(map, sb);
            }
        } else if (value.isSequence()) {
            YamlSequence seq = value.asSequence();
            if (seq.isEmpty()) {
                sb.append("[]");
            } else if (prettyPrint) {
                sb.append('\n');
                writeSequence(seq, sb, depth + 1);
            } else {
                writeFlowSequence(seq, sb);
            }
        }
    }

    // ============ value writing (as a sequence item) ============

    /**
     * Writes a value that follows {@code - } in a block sequence.
     * <p>
     * Scalars and empty collections are written inline.
     * Non-empty mappings put their first key inline with {@code - } and
     * subsequent keys on new lines (pretty-print) or use flow style (compact).
     * Non-empty sequences use block style on the next line (pretty-print)
     * or flow style inline (compact).
     */
    private void writeValueAsSequenceItem(YamlNode value, StringBuilder sb, int depth) {
        if (value == null || value.isNull()) {
            sb.append("null");
        } else if (value.isScalar()) {
            writeScalar(value.asScalar(), sb);
        } else if (value.isMapping()) {
            YamlMapping map = value.asMapping();
            if (map.isEmpty()) {
                sb.append("{}");
            } else if (prettyPrint) {
                // First entry inline with "- ", rest on new lines at depth + 1
                writeMappingWithInlineFirst(map, sb, depth);
            } else {
                writeFlowMapping(map, sb);
            }
        } else if (value.isSequence()) {
            YamlSequence seq = value.asSequence();
            if (seq.isEmpty()) {
                sb.append("[]");
            } else if (prettyPrint) {
                // Nested sequence on next line
                sb.append('\n');
                writeSequence(seq, sb, depth + 1);
            } else {
                writeFlowSequence(seq, sb);
            }
        }
    }

    /**
     * Writes a mapping where the first entry's key is placed inline
     * (e.g. after {@code - }), and subsequent entries are indented to align
     * with the first key.
     */
    private void writeMappingWithInlineFirst(YamlMapping mapping, StringBuilder sb, int depth) {
        boolean first = true;
        for (Map.Entry<String, YamlNode> entry : mapping) {
            if (!first) {
                sb.append('\n');
                indent(sb, depth + 1);
            }
            first = false;
            writeKey(entry.getKey(), sb);
            sb.append(": ");
            writeValueAfterColon(entry.getValue(), sb, depth + 1);
        }
    }

    // ============ flow style ============

    /**
     * Writes a mapping in flow style: {@code {key: value, key2: value2}}.
     */
    private void writeFlowMapping(YamlMapping mapping, StringBuilder sb) {
        if (mapping.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append('{');
        boolean first = true;
        for (Map.Entry<String, YamlNode> entry : mapping) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            writeKey(entry.getKey(), sb);
            sb.append(": ");
            writeFlowValue(entry.getValue(), sb);
        }
        sb.append('}');
    }

    /**
     * Writes a sequence in flow style: {@code [a, b, c]}.
     */
    private void writeFlowSequence(YamlSequence sequence, StringBuilder sb) {
        if (sequence.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append('[');
        boolean first = true;
        for (YamlNode element : sequence) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            writeFlowValue(element, sb);
        }
        sb.append(']');
    }

    /**
     * Writes a value in flow context (inside {@code {}} or {@code []}).
     * Nested collections are also rendered in flow style.
     */
    private void writeFlowValue(YamlNode value, StringBuilder sb) {
        if (value == null || value.isNull()) {
            sb.append("null");
        } else if (value.isScalar()) {
            writeScalar(value.asScalar(), sb);
        } else if (value.isMapping()) {
            writeFlowMapping(value.asMapping(), sb);
        } else if (value.isSequence()) {
            writeFlowSequence(value.asSequence(), sb);
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
        // Leading/trailing whitespace
        if (str.charAt(0) == ' ' || str.charAt(str.length() - 1) == ' ') return true;
        // Special YAML indicator characters at start
        if (str.startsWith("#") || str.startsWith("&") || str.startsWith("*") ||
                str.startsWith("!") || str.startsWith("|") || str.startsWith(">") ||
                str.startsWith("'") || str.startsWith("\"")) return true;
        if (str.startsWith("{") || str.startsWith("[") || str.startsWith("?") ||
                str.startsWith("-") || str.startsWith(":")) return true;
        // Colon followed by space, or space followed by hash
        if (str.contains(": ") || str.contains(" #")) return true;
        // Boolean / null keywords
        if (str.equals("true") || str.equals("True") || str.equals("TRUE") ||
                str.equals("false") || str.equals("False") || str.equals("FALSE") ||
                str.equals("yes") || str.equals("Yes") || str.equals("YES") ||
                str.equals("no") || str.equals("No") || str.equals("NO") ||
                str.equals("on") || str.equals("On") || str.equals("ON") ||
                str.equals("off") || str.equals("Off") || str.equals("OFF") ||
                str.equals("null") || str.equals("Null") || str.equals("NULL") ||
                str.equals("~")) return true;
        // Numeric strings (quote to preserve as string)
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            // not a number
        }
        return false;
    }

    private String quoteString(String str) {
        // Use single quotes by default; double quotes if string contains single quotes
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