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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Line-based YAML parser that builds a {@link YamlDocument} tree.
 * <p>
 * Supported features:
 * <ul>
 *   <li>Block mappings ({@code key: value})</li>
 *   <li>Block sequences ({@code - item})</li>
 *   <li>Flow mappings ({@code {key: value}})</li>
 *   <li>Flow sequences ({@code [a, b]})</li>
 *   <li>Quoted strings (single and double)</li>
 *   <li>Comments ({@code #})</li>
 *   <li>Multi-line literal ({@code |}) and folded ({@code >}) scalars</li>
 *   <li>Explicit null ({@code ~})</li>
 *   <li>Booleans (true/false, yes/no, on/off)</li>
 *   <li>Numbers (integers and floats)</li>
 *   <li>Multi-document streams ({@code ---})</li>
 * </ul>
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class YamlParser {

    private final String yaml;
    private final String[] lines;
    private int lineIndex;
    private int currentIndent;

    private YamlParser(String yaml) {
        this.yaml = Objects.requireNonNull(yaml, "yaml must not be null");
        this.lines = yaml.split("\n", -1);
        this.lineIndex = 0;
        this.currentIndent = 0;
    }

    /**
     * Parses a YAML string into a {@link YamlDocument}.
     *
     * @param yaml the YAML string to parse
     * @return the parsed YamlDocument
     * @throws YamlException if the input is not valid YAML
     */
    public static YamlDocument parse(String yaml) {
        YamlParser parser = new YamlParser(yaml);
        List<YamlNode> documents = new ArrayList<>();

        while (parser.lineIndex < parser.lines.length) {
            String line = parser.currentLine();
            if (line == null) break;

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                parser.lineIndex++;
                continue;
            }

            // Document separator
            if (trimmed.equals("---")) {
                parser.lineIndex++;
                continue;
            }

            // End of document
            if (trimmed.equals("...")) {
                parser.lineIndex++;
                continue;
            }

            // Parse a document
            YamlNode doc = parser.parseNode(0);
            if (doc != null) {
                documents.add(doc);
            }
        }

        if (documents.isEmpty()) {
            throw new YamlException("Empty YAML document");
        }
        return new YamlDocument(documents);
    }

    // ============ line access ============

    private String currentLine() {
        if (lineIndex >= lines.length) return null;
        String line = lines[lineIndex];
        // strip trailing carriage return
        if (!line.isEmpty() && line.charAt(line.length() - 1) == '\r') {
            line = line.substring(0, line.length() - 1);
        }
        return line;
    }

    private int lineIndent(String line) {
        int indent = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == ' ') {
                indent++;
            } else if (line.charAt(i) == '\t') {
                indent += 2; // treat tab as 2 spaces
            } else {
                break;
            }
        }
        return indent;
    }

    private String stripIndent(String line) {
        return line.stripLeading();
    }

    // ============ node parsing ============

    private YamlNode parseNode(int parentIndent) {
        // Skip blank and comment-only lines at current position
        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;
            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }
            break;
        }

        if (lineIndex >= lines.length) return null;

        String line = currentLine();
        int indent = lineIndent(line);

        // If we've gone past the parent's indentation level, we're done with this block
        if (indent < parentIndent) return null;

        String content = stripIndent(line);

        // Check for sequence item
        if (content.startsWith("- ")) {
            return parseSequence(parentIndent);
        }

        // Check for flow sequence
        if (content.startsWith("[")) {
            lineIndex++;
            return parseFlowSequence(content);
        }

        // Check for flow mapping
        if (content.startsWith("{")) {
            lineIndex++;
            return parseFlowMapping(content);
        }

        // Check for mapping (key: value)
        int colonIndex = findMappingColon(content);
        if (colonIndex >= 0) {
            return parseMapping(parentIndent);
        }

        // Simple scalar
        lineIndex++;
        return parseScalarValue(content.trim());
    }

    // ============ mapping parsing ============

    private YamlMapping parseMapping(int parentIndent) {
        YamlMapping mapping = new YamlMapping();

        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }

            int indent = lineIndent(line);
            if (indent < parentIndent) break;
            if (indent > parentIndent) break;

            // Check if this line is a sequence item that belongs to a key
            if (trimmed.startsWith("- ")) {
                break;
            }

            String content = stripIndent(line);
            int colonIndex = findMappingColon(content);

            if (colonIndex < 0) {
                // Not a mapping line, stop
                break;
            }

            String key = content.substring(0, colonIndex).trim();
            String afterColon = content.substring(colonIndex + 1).trim();

            lineIndex++;
            YamlNode value;

            if (afterColon.isEmpty()) {
                // Value is on subsequent lines (indented)
                // Check for literal or folded scalar
                String nextLine = currentLine();
                if (nextLine != null) {
                    String nextTrimmed = nextLine.trim();
                    if (nextTrimmed.equals("|")) {
                        value = parseLiteralScalar(indent + 2);
                    } else if (nextTrimmed.equals(">")) {
                        value = parseFoldedScalar(indent + 2);
                    } else {
                        // Check if next line is indented (block content)
                        int nextIndent = lineIndent(nextLine);
                        if (nextIndent > indent) {
                            value = parseBlockContent(nextIndent);
                        } else {
                            value = YamlNull.INSTANCE;
                        }
                    }
                } else {
                    value = YamlNull.INSTANCE;
                }
            } else if (afterColon.startsWith("[")) {
                value = parseFlowSequence(afterColon);
            } else if (afterColon.startsWith("{")) {
                value = parseFlowMapping(afterColon);
            } else if (afterColon.startsWith("- ")) {
                // Compact inline sequence
                value = parseCompactSequence(afterColon, indent);
            } else {
                value = parseScalarValue(afterColon);
            }

            mapping.add(key, value);
        }

        return mapping;
    }

    // ============ sequence parsing ============

    private YamlSequence parseSequence(int parentIndent) {
        YamlSequence sequence = new YamlSequence();

        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }

            int indent = lineIndent(line);
            if (indent < parentIndent) break;

            String content = stripIndent(line);

            if (!content.startsWith("- ")) {
                break;
            }

            // Remove "- " prefix
            String itemContent = content.substring(2).trim();
            lineIndex++;

            YamlNode item;
            if (itemContent.isEmpty()) {
                // Item content is on subsequent lines
                String nextLine = currentLine();
                if (nextLine != null) {
                    int nextIndent = lineIndent(nextLine);
                    if (nextIndent > indent) {
                        item = parseBlockContent(nextIndent);
                    } else {
                        item = YamlNull.INSTANCE;
                    }
                } else {
                    item = YamlNull.INSTANCE;
                }
            } else if (itemContent.startsWith("[")) {
                item = parseFlowSequence(itemContent);
            } else if (itemContent.startsWith("{")) {
                item = parseFlowMapping(itemContent);
            } else if (itemContent.startsWith("- ")) {
                // Nested compact sequence
                item = parseCompactSequence(itemContent, indent + 2);
            } else {
                // Check for inline mapping: "- key: value"
                int inlineColon = findMappingColon(itemContent);
                if (inlineColon >= 0) {
                    item = parseInlineMapping(itemContent, indent);
                } else {
                    item = parseScalarValue(itemContent);
                }
            }

            sequence.add(item);
        }

        return sequence;
    }

    // ============ block content parsing ============

    private YamlNode parseBlockContent(int blockIndent) {
        // Look ahead to determine the type of block content
        String line = currentLine();
        if (line == null) return YamlNull.INSTANCE;

        String trimmed = line.trim();
        if (trimmed.startsWith("- ")) {
            return parseSequence(blockIndent);
        }

        int colonIndex = findMappingColon(trimmed);
        if (colonIndex >= 0) {
            return parseMapping(blockIndent);
        }

        // Scalar block (multi-line plain text)
        return parseBlockScalar(blockIndent);
    }

    // ============ inline mapping (compact mapping in sequence) ============

    /**
     * Parses a mapping whose first entry is inline (e.g. after {@code "- "}).
     * <p>
     * Example:
     * <pre>
     *   - id: 1
     *     name: first
     * </pre>
     * The first entry {@code id: 1} is parsed from {@code firstEntry}, and
     * subsequent entries are parsed from the following lines at indent
     * {@code sequenceIndent + 2} (aligned with the content after "- ").
     *
     * @param firstEntry    the inline content after "- " (e.g. "id: 1")
     * @param sequenceIndent the indent of the "- " line
     */
    private YamlMapping parseInlineMapping(String firstEntry, int sequenceIndent) {
        YamlMapping mapping = new YamlMapping();
        int mappingIndent = sequenceIndent + 2;

        // Parse the first entry from the inline content
        int colonIdx = findMappingColon(firstEntry);
        String key = firstEntry.substring(0, colonIdx).trim();
        String afterColon = firstEntry.substring(colonIdx + 1).trim();

        YamlNode value = parseMappingValue(afterColon, mappingIndent);
        mapping.add(key, value);

        // Parse subsequent entries at mappingIndent
        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }

            int indent = lineIndent(line);
            if (indent != mappingIndent) break;
            if (trimmed.startsWith("- ")) break;

            String content = stripIndent(line);
            int ci = findMappingColon(content);
            if (ci < 0) break;

            String k = content.substring(0, ci).trim();
            String ac = content.substring(ci + 1).trim();
            lineIndex++;

            YamlNode v = parseMappingValue(ac, mappingIndent);
            mapping.add(k, v);
        }

        return mapping;
    }

    /**
     * Parses a mapping value given the text after the colon.
     * Handles inline scalars, flow collections, and nested block content.
     *
     * @param afterColon   the text after "key:" (already trimmed)
     * @param keyIndent    the indent of the key line
     */
    private YamlNode parseMappingValue(String afterColon, int keyIndent) {
        if (afterColon.isEmpty()) {
            // Value is on subsequent lines
            String nextLine = currentLine();
            if (nextLine != null) {
                String nextTrimmed = nextLine.trim();
                if (nextTrimmed.equals("|")) {
                    lineIndex++;
                    return parseLiteralScalar(keyIndent + 2);
                }
                if (nextTrimmed.equals(">")) {
                    lineIndex++;
                    return parseFoldedScalar(keyIndent + 2);
                }
                int nextIndent = lineIndent(nextLine);
                if (nextIndent > keyIndent) {
                    return parseBlockContent(nextIndent);
                }
            }
            return YamlNull.INSTANCE;
        }
        if (afterColon.startsWith("[")) {
            return parseFlowSequence(afterColon);
        }
        if (afterColon.startsWith("{")) {
            return parseFlowMapping(afterColon);
        }
        return parseScalarValue(afterColon);
    }

    // ============ flow structures ============

    private YamlSequence parseFlowSequence(String content) {
        // content starts with '['
        int end = findMatchingBracket(content, 0, '[', ']');
        if (end < 0) {
            throw new YamlException("Unterminated flow sequence");
        }

        String inner = content.substring(1, end).trim();
        YamlSequence sequence = new YamlSequence();

        if (!inner.isEmpty()) {
            List<String> items = splitFlowElements(inner);
            for (String item : items) {
                sequence.add(parseFlowValue(item.trim()));
            }
        }

        // Check for remaining content after the closing bracket
        String remaining = content.substring(end + 1).trim();
        if (!remaining.isEmpty() && !remaining.startsWith("#")) {
            // This handles things like: key: [a, b] more stuff
            // In YAML, after a flow sequence, the line should be done
        }

        return sequence;
    }

    private YamlMapping parseFlowMapping(String content) {
        // content starts with '{'
        int end = findMatchingBracket(content, 0, '{', '}');
        if (end < 0) {
            throw new YamlException("Unterminated flow mapping");
        }

        String inner = content.substring(1, end).trim();
        YamlMapping mapping = new YamlMapping();

        if (!inner.isEmpty()) {
            List<String> entries = splitFlowElements(inner);
            for (String entry : entries) {
                int colonIdx = findMappingColon(entry.trim());
                if (colonIdx < 0) {
                    throw new YamlException("Invalid flow mapping entry: " + entry);
                }
                String key = entry.substring(0, colonIdx).trim();
                String value = entry.substring(colonIdx + 1).trim();
                mapping.add(key, parseFlowValue(value));
            }
        }

        return mapping;
    }

    /**
     * Parses a compact inline sequence like "- item1\n- item2\n" 
     * that appears after a mapping key.
     */
    private YamlSequence parseCompactSequence(String firstItem, int parentIndent) {
        YamlSequence sequence = new YamlSequence();

        // Process the first item
        String itemContent = firstItem.substring(2).trim();
        if (itemContent.isEmpty()) {
            // Content on next line
            String nextLine = currentLine();
            if (nextLine != null) {
                int nextIndent = lineIndent(nextLine);
                if (nextIndent > parentIndent) {
                    sequence.add(parseBlockContent(nextIndent));
                } else {
                    sequence.add(YamlNull.INSTANCE);
                }
            } else {
                sequence.add(YamlNull.INSTANCE);
            }
        } else if (itemContent.startsWith("[")) {
            sequence.add(parseFlowSequence(itemContent));
        } else if (itemContent.startsWith("{")) {
            sequence.add(parseFlowMapping(itemContent));
        } else {
            sequence.add(parseScalarValue(itemContent));
        }

        // Continue parsing more sequence items
        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;

            String trimmed = line.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }

            int indent = lineIndent(line);
            if (indent < parentIndent) break;

            String content = stripIndent(line);
            if (!content.startsWith("- ")) break;

            String itemVal = content.substring(2).trim();
            lineIndex++;

            if (itemVal.isEmpty()) {
                String nextLine = currentLine();
                if (nextLine != null) {
                    int nextIndent = lineIndent(nextLine);
                    if (nextIndent > indent) {
                        sequence.add(parseBlockContent(nextIndent));
                    } else {
                        sequence.add(YamlNull.INSTANCE);
                    }
                } else {
                    sequence.add(YamlNull.INSTANCE);
                }
            } else if (itemVal.startsWith("[")) {
                sequence.add(parseFlowSequence(itemVal));
            } else if (itemVal.startsWith("{")) {
                sequence.add(parseFlowMapping(itemVal));
            } else {
                // Check for inline mapping: "- key: value"
                int inlineColon = findMappingColon(itemVal);
                if (inlineColon >= 0) {
                    sequence.add(parseInlineMapping(itemVal, indent));
                } else {
                    sequence.add(parseScalarValue(itemVal));
                }
            }
        }

        return sequence;
    }

    // ============ scalar parsing ============

    private YamlNode parseScalarValue(String value) {
        if (value.isEmpty()) {
            return YamlNull.INSTANCE;
        }

        // Null values
        if (value.equals("~") || value.equals("null") || value.equals("Null") || value.equals("NULL")) {
            return YamlNull.INSTANCE;
        }

        // Boolean values
        if (value.equals("true") || value.equals("True") || value.equals("TRUE") ||
                value.equals("yes") || value.equals("Yes") || value.equals("YES") ||
                value.equals("on") || value.equals("On") || value.equals("ON")) {
            return new YamlScalar(true);
        }
        if (value.equals("false") || value.equals("False") || value.equals("FALSE") ||
                value.equals("no") || value.equals("No") || value.equals("NO") ||
                value.equals("off") || value.equals("Off") || value.equals("OFF")) {
            return new YamlScalar(false);
        }

        // Quoted strings
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if (first == '"' && last == '"') {
                String inner = value.substring(1, value.length() - 1);
                return new YamlScalar(unescapeDoubleQuoted(inner));
            }
            if (first == '\'' && last == '\'') {
                String inner = value.substring(1, value.length() - 1);
                return new YamlScalar(inner.replace("''", "'"));
            }
        }

        // Numbers
        try {
            if (value.contains(".") || value.contains("e") || value.contains("E")) {
                return new YamlScalar(Double.parseDouble(value));
            }
            // Try integer
            long longVal = Long.parseLong(value);
            if (longVal >= Integer.MIN_VALUE && longVal <= Integer.MAX_VALUE) {
                return new YamlScalar((int) longVal);
            }
            return new YamlScalar(longVal);
        } catch (NumberFormatException e) {
            // Not a number, treat as string
        }

        return new YamlScalar(value);
    }

    private YamlNode parseFlowValue(String value) {
        // Same as parseScalarValue but also handles flow structures
        if (value.isEmpty()) {
            return YamlNull.INSTANCE;
        }

        if (value.startsWith("[") && value.endsWith("]")) {
            return parseFlowSequence(value);
        }
        if (value.startsWith("{") && value.endsWith("}")) {
            return parseFlowMapping(value);
        }

        return parseScalarValue(value);
    }

    // ============ multi-line scalars ============

    private YamlNode parseLiteralScalar(int blockIndent) {
        // Skip the "|" line
        lineIndex++;
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;

        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;

            int indent = lineIndent(line);
            if (indent < blockIndent) break;

            String trimmed = line.trim();
            if (trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }

            if (!firstLine) {
                sb.append('\n');
            }
            firstLine = false;

            // Include the content with its relative indentation
            sb.append(line.substring(Math.min(indent, blockIndent)));
            lineIndex++;
        }

        return new YamlScalar(sb.toString());
    }

    private YamlNode parseFoldedScalar(int blockIndent) {
        // Skip the ">" line
        lineIndex++;
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;

        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;

            int indent = lineIndent(line);
            if (indent < blockIndent) break;

            String trimmed = line.trim();
            if (trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }

            if (!firstLine) {
                if (trimmed.isEmpty()) {
                    sb.append('\n');
                } else {
                    sb.append(' ');
                }
            }
            firstLine = false;

            sb.append(line.substring(Math.min(indent, blockIndent)));
            lineIndex++;
        }

        return new YamlScalar(sb.toString().trim());
    }

    private YamlNode parseBlockScalar(int blockIndent) {
        StringBuilder sb = new StringBuilder();
        boolean firstLine = true;

        while (lineIndex < lines.length) {
            String line = currentLine();
            if (line == null) break;

            int indent = lineIndent(line);
            if (indent < blockIndent) break;

            String trimmed = line.trim();
            if (trimmed.startsWith("#")) {
                lineIndex++;
                continue;
            }

            if (!firstLine) {
                sb.append('\n');
            }
            firstLine = false;

            sb.append(line.substring(Math.min(indent, blockIndent)));
            lineIndex++;
        }

        String result = sb.toString().trim();
        if (result.isEmpty()) {
            return YamlNull.INSTANCE;
        }
        return parseScalarValue(result);
    }

    // ============ helpers ============

    /**
     * Finds the colon that separates a mapping key from its value.
     * Takes quoted strings into account.
     */
    private int findMappingColon(String content) {
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inFlowSeq = false;
        boolean inFlowMap = false;
        int flowDepth = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
                continue;
            }

            if (inDoubleQuote) {
                if (c == '\\') {
                    i++; // skip escaped char
                    continue;
                }
                if (c == '"') {
                    inDoubleQuote = false;
                }
                continue;
            }

            if (c == '\'') {
                inSingleQuote = true;
                continue;
            }

            if (c == '"') {
                inDoubleQuote = true;
                continue;
            }

            if (c == '[') {
                flowDepth++;
                continue;
            }
            if (c == ']') {
                flowDepth--;
                continue;
            }
            if (c == '{') {
                flowDepth++;
                continue;
            }
            if (c == '}') {
                flowDepth--;
                continue;
            }

            // In YAML 1.2, a mapping separator is ":" followed by a space/tab
            // or ":" at the end of the line. This avoids matching "http://...".
            if (c == ':' && flowDepth == 0) {
                if (i + 1 >= content.length()) {
                    // Colon at end of line → mapping with null value
                    return i;
                }
                char next = content.charAt(i + 1);
                if (next == ' ' || next == '\t') {
                    // Colon followed by space → mapping separator
                    return i;
                }
                // Colon followed by non-space (e.g. "http://") is not a separator
            }
        }

        return -1;
    }

    /**
     * Finds the matching closing bracket for a flow structure.
     */
    private int findMatchingBracket(String s, int start, char open, char close) {
        int depth = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;

        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);

            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
                continue;
            }

            if (inDoubleQuote) {
                if (c == '\\') {
                    i++;
                    continue;
                }
                if (c == '"') {
                    inDoubleQuote = false;
                }
                continue;
            }

            if (c == '\'') {
                inSingleQuote = true;
                continue;
            }

            if (c == '"') {
                inDoubleQuote = true;
                continue;
            }

            if (c == open) {
                depth++;
                continue;
            }

            if (c == close) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Splits flow elements separated by commas, respecting nested structures and quotes.
     */
    private List<String> splitFlowElements(String s) {
        List<String> elements = new ArrayList<>();
        int depth = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int lastSplit = 0;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
                continue;
            }

            if (inDoubleQuote) {
                if (c == '\\') {
                    i++;
                    continue;
                }
                if (c == '"') {
                    inDoubleQuote = false;
                }
                continue;
            }

            if (c == '\'') {
                inSingleQuote = true;
                continue;
            }

            if (c == '"') {
                inDoubleQuote = true;
                continue;
            }

            if (c == '[' || c == '{') {
                depth++;
                continue;
            }

            if (c == ']' || c == '}') {
                depth--;
                continue;
            }

            if (c == ',' && depth == 0) {
                elements.add(s.substring(lastSplit, i));
                lastSplit = i + 1;
            }
        }

        if (lastSplit < s.length()) {
            String last = s.substring(lastSplit).trim();
            if (!last.isEmpty()) {
                elements.add(last);
            }
        }

        return elements;
    }

    private String unescapeDoubleQuoted(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '0' : sb.append('\0'); i++; break;
                    case 'a' : sb.append('\u0007'); i++; break;
                    case 'b' : sb.append('\b'); i++; break;
                    case 't' : case '\t': sb.append('\t'); i++; break;
                    case 'n' : sb.append('\n'); i++; break;
                    case 'v' : sb.append('\u000B'); i++; break;
                    case 'f' : sb.append('\f'); i++; break;
                    case 'r' : sb.append('\r'); i++; break;
                    case 'e' : sb.append('\u001B'); i++; break;
                    case ' ': sb.append(' '); i++; break;
                    case '"' : sb.append('"'); i++; break;
                    case '/' : sb.append('/'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case 'N' : sb.append('\u0085'); i++; break;
                    case '_' : sb.append('\u00A0'); i++; break;
                    case 'L' : sb.append('\u2028'); i++; break;
                    case 'P' : sb.append('\u2029'); i++; break;
                    case 'x' :
                        if (i + 3 < s.length()) {
                            sb.append((char) Integer.parseInt(s.substring(i + 2, i + 4), 16));
                            i += 3;
                        }
                        break;
                    case 'u' :
                        if (i + 5 < s.length()) {
                            sb.append((char) Integer.parseInt(s.substring(i + 2, i + 6), 16));
                            i += 5;
                        }
                        break;
                    default:
                        // unknown escape, keep as-is
                        sb.append('\\').append(next);
                        i++;
                        break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}