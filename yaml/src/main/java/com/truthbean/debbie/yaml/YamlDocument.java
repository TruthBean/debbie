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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a YAML document, which may contain one or more documents
 * separated by {@code ---} directives.
 * <p>
 * A single-document YAML file will have one root node. Multi-document
 * streams store each document's root node in order.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class YamlDocument {

    private final List<YamlNode> documents;

    /**
     * Creates a single-document YAML document.
     * @param root the root node of the document
     */
    public YamlDocument(YamlNode root) {
        this.documents = new ArrayList<>(1);
        this.documents.add(Objects.requireNonNull(root, "root must not be null"));
    }

    /**
     * Creates a multi-document YAML document.
     * @param documents the list of document root nodes
     */
    public YamlDocument(List<YamlNode> documents) {
        this.documents = new ArrayList<>(Objects.requireNonNull(documents, "documents must not be null"));
        if (this.documents.isEmpty()) {
            throw new IllegalArgumentException("documents must not be empty");
        }
    }

    /**
     * @return the number of documents in this stream
     */
    public int documentCount() {
        return documents.size();
    }

    /**
     * @return true if this is a single-document stream
     */
    public boolean isSingleDocument() {
        return documents.size() == 1;
    }

    /**
     * @return the root node of the first (or only) document
     */
    public YamlNode getRoot() {
        return documents.get(0);
    }

    /**
     * @param index the document index
     * @return the root node of the document at the given index
     */
    public YamlNode getDocument(int index) {
        return documents.get(index);
    }

    /**
     * @return an unmodifiable list of all document root nodes
     */
    public List<YamlNode> getDocuments() {
        return Collections.unmodifiableList(documents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YamlDocument that = (YamlDocument) o;
        return documents.equals(that.documents);
    }

    @Override
    public int hashCode() {
        return documents.hashCode();
    }

    @Override
    public String toString() {
        return documents.toString();
    }
}