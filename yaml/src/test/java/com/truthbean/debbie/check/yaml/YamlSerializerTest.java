/**
 * Copyright (c) 2026 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.check.yaml;

import com.truthbean.debbie.yaml.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link YamlSerializer}, focusing on producing valid YAML
 * in both compact and pretty-print modes.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
@DisplayName("YamlSerializer tests")
class YamlSerializerTest {

    private final YamlSerializer compact = new YamlSerializer(false);
    private final YamlSerializer pretty = new YamlSerializer(true);

    private YamlMapping buildSample() {
        YamlMapping nested = new YamlMapping()
                .add("inner", "value")
                .add("count", 42);
        YamlSequence seq = new YamlSequence()
                .add("a")
                .add("b")
                .add(3);
        return new YamlMapping()
                .add("name", "test")
                .add("active", true)
                .add("nested", nested)
                .add("list", seq);
    }

    @Test
    @DisplayName("Compact mode produces valid YAML (round-trip)")
    void testCompactRoundTrip() {
        YamlMapping original = buildSample();
        String yaml = compact.serialize(original);
        System.out.println("=== Compact ===\n" + yaml);

        // Parse back
        YamlDocument doc = YamlParser.parse(yaml);
        YamlMapping parsed = doc.getRoot().asMapping();

        assertEquals("test", parsed.getAsString("name"));
        assertTrue(parsed.getAsBoolean("active"));
        YamlMapping nested = parsed.getAsMapping("nested");
        assertEquals("value", nested.getAsString("inner"));
        assertEquals(42, nested.getAsInt("count"));
        YamlSequence seq = parsed.getAsSequence("list");
        assertEquals(3, seq.size());
        assertEquals("a", seq.getAsString(0));
        assertEquals("b", seq.getAsString(1));
        assertEquals(3, seq.getAsInt(2));
    }

    @Test
    @DisplayName("Pretty mode produces valid YAML (round-trip)")
    void testPrettyRoundTrip() {
        YamlMapping original = buildSample();
        String yaml = pretty.serialize(original);
        System.out.println("=== Pretty ===\n" + yaml);

        // Parse back
        YamlDocument doc = YamlParser.parse(yaml);
        YamlMapping parsed = doc.getRoot().asMapping();

        assertEquals("test", parsed.getAsString("name"));
        assertTrue(parsed.getAsBoolean("active"));
        YamlMapping nested = parsed.getAsMapping("nested");
        assertEquals("value", nested.getAsString("inner"));
        assertEquals(42, nested.getAsInt("count"));
        YamlSequence seq = parsed.getAsSequence("list");
        assertEquals(3, seq.size());
        assertEquals("a", seq.getAsString(0));
        assertEquals("b", seq.getAsString(1));
        assertEquals(3, seq.getAsInt(2));
    }

    @Test
    @DisplayName("Compact mode has no double spaces after colon")
    void testNoDoubleSpaceInCompact() {
        YamlMapping mapping = new YamlMapping().add("key", "value");
        String yaml = compact.serialize(mapping);
        assertFalse(yaml.contains(":  "), "Compact mode should not have double space after colon: " + yaml);
    }

    @Test
    @DisplayName("Pretty mode has no double spaces after colon")
    void testNoDoubleSpaceInPretty() {
        YamlMapping mapping = new YamlMapping().add("key", "value");
        String yaml = pretty.serialize(mapping);
        assertFalse(yaml.contains(":  "), "Pretty mode should not have double space after colon: " + yaml);
    }

    @Test
    @DisplayName("Compact mode uses flow style for nested collections")
    void testCompactUsesFlowStyle() {
        YamlMapping mapping = buildSample();
        String yaml = compact.serialize(mapping);
        // Compact mode should use { } and [ ] for nested collections
        assertTrue(yaml.contains("{"), "Compact mode should use flow mapping: " + yaml);
        assertTrue(yaml.contains("["), "Compact mode should use flow sequence: " + yaml);
    }

    @Test
    @DisplayName("Pretty mode uses block style for nested collections")
    void testPrettyUsesBlockStyle() {
        YamlMapping mapping = buildSample();
        String yaml = pretty.serialize(mapping);
        // Pretty mode should NOT use flow style for non-empty collections
        assertFalse(yaml.contains("{inner"), "Pretty mode should not use flow mapping: " + yaml);
        assertFalse(yaml.contains("[a,"), "Pretty mode should not use flow sequence: " + yaml);
    }

    @Test
    @DisplayName("Empty mapping and sequence")
    void testEmptyCollections() {
        YamlMapping mapping = new YamlMapping()
                .add("emptyMap", new YamlMapping())
                .add("emptyList", new YamlSequence());
        String compactYaml = compact.serialize(mapping);
        String prettyYaml = pretty.serialize(mapping);

        YamlDocument doc1 = YamlParser.parse(compactYaml);
        assertTrue(doc1.getRoot().asMapping().getAsMapping("emptyMap").isEmpty());
        assertTrue(doc1.getRoot().asMapping().getAsSequence("emptyList").isEmpty());

        YamlDocument doc2 = YamlParser.parse(prettyYaml);
        assertTrue(doc2.getRoot().asMapping().getAsMapping("emptyMap").isEmpty());
        assertTrue(doc2.getRoot().asMapping().getAsSequence("emptyList").isEmpty());
    }

    @Test
    @DisplayName("Null values")
    void testNullValues() {
        YamlMapping mapping = new YamlMapping()
                .addNull("nothing")
                .add("present", "value");
        String yaml = compact.serialize(mapping);
        System.out.println("=== Null ===\n" + yaml);

        YamlDocument doc = YamlParser.parse(yaml);
        YamlMapping parsed = doc.getRoot().asMapping();
        assertTrue(parsed.get("nothing").isNull());
        assertEquals("value", parsed.getAsString("present"));
    }

    @Test
    @DisplayName("Sequence of mappings (pretty mode)")
    void testSequenceOfMappings() {
        YamlSequence seq = new YamlSequence();
        seq.add(new YamlMapping().add("id", 1).add("name", "first"));
        seq.add(new YamlMapping().add("id", 2).add("name", "second"));
        YamlMapping root = new YamlMapping().add("items", seq);

        String prettyYaml = pretty.serialize(root);
        System.out.println("=== Seq of Maps ===\n" + prettyYaml);

        YamlDocument doc = YamlParser.parse(prettyYaml);
        YamlSequence parsed = doc.getRoot().asMapping().getAsSequence("items");
        assertEquals(2, parsed.size());
        assertEquals(1, parsed.getAsMapping(0).getAsInt("id"));
        assertEquals("first", parsed.getAsMapping(0).getAsString("name"));
        assertEquals(2, parsed.getAsMapping(1).getAsInt("id"));
        assertEquals("second", parsed.getAsMapping(1).getAsString("name"));
    }

    @Test
    @DisplayName("Deeply nested structure round-trip")
    void testDeepNesting() {
        YamlMapping deep = new YamlMapping()
                .add("level1", new YamlMapping()
                        .add("level2", new YamlMapping()
                                .add("level3", new YamlSequence()
                                        .add(new YamlMapping().add("deep", "value")))));
        YamlMapping root = new YamlMapping().add("root", deep);

        // Test both modes
        for (YamlSerializer serializer : new YamlSerializer[]{compact, pretty}) {
            String yaml = serializer.serialize(root);
            YamlDocument doc = YamlParser.parse(yaml);
            YamlMapping parsed = doc.getRoot().asMapping();
            YamlMapping l1 = parsed.getAsMapping("root").getAsMapping("level1");
            YamlMapping l2 = l1.getAsMapping("level2");
            YamlSequence l3 = l2.getAsSequence("level3");
            assertEquals("value", l3.getAsMapping(0).getAsString("deep"));
        }
    }

    @Test
    @DisplayName("Multi-document serialization")
    void testMultiDocument() {
        YamlDocument multi = new YamlDocument(java.util.List.of(
                new YamlMapping().add("a", 1),
                new YamlMapping().add("b", 2)
        ));
        String yaml = pretty.serialize(multi);
        System.out.println("=== Multi-doc ===\n" + yaml);
        assertTrue(yaml.contains("---"));

        YamlDocument parsed = YamlParser.parse(yaml);
        assertEquals(2, parsed.documentCount());
        assertEquals(1, parsed.getDocument(0).asMapping().getAsInt("a"));
        assertEquals(2, parsed.getDocument(1).asMapping().getAsInt("b"));
    }
}