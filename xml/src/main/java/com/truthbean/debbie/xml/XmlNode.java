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

import java.util.*;

/**
 * Represents a single XML element node.
 * <p>
 * Each node has a tag name, an ordered map of attributes, a list of child nodes,
 * and optional text content. A node may contain either text content or child elements,
 * but not both (mixed content is not supported).
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public class XmlNode implements Iterable<XmlNode> {

    private String name;
    private final LinkedHashMap<String, String> attributes;
    private final List<XmlNode> children;
    private String textContent;
    private XmlNode parent;

    /**
     * Creates a new XmlNode with the given tag name.
     *
     * @param name the tag name
     */
    public XmlNode(String name) {
        this.name = Objects.requireNonNull(name, "tag name must not be null");
        this.attributes = new LinkedHashMap<>();
        this.children = new ArrayList<>();
        this.textContent = null;
    }

    // ============ name ============

    /**
     * @return the tag name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the new tag name
     */
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "tag name must not be null");
    }

    // ============ attributes ============

    /**
     * Sets an attribute value.
     *
     * @param name  the attribute name
     * @param value the attribute value
     * @return this, for chaining
     */
    public XmlNode setAttribute(String name, String value) {
        attributes.put(Objects.requireNonNull(name, "attribute name must not be null"),
                value == null ? "" : value);
        return this;
    }

    /**
     * @param name the attribute name
     * @return the attribute value, or null if not present
     */
    public String getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * @param name the attribute name
     * @return true if this node has the given attribute
     */
    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    /**
     * Removes an attribute.
     *
     * @param name the attribute name
     * @return the removed value, or null if not present
     */
    public String removeAttribute(String name) {
        return attributes.remove(name);
    }

    /**
     * @return an unmodifiable view of the attributes
     */
    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * @return the number of attributes
     */
    public int attributeCount() {
        return attributes.size();
    }

    // ============ children ============

    /**
     * Appends a child node.
     *
     * @param child the child node to add
     * @return this, for chaining
     */
    public XmlNode appendChild(XmlNode child) {
        if (child.parent != null) {
            child.parent.children.remove(child);
        }
        child.parent = this;
        children.add(child);
        return this;
    }

    /**
     * Inserts a child node at the given index.
     *
     * @param index the index
     * @param child the child node
     * @return this, for chaining
     */
    public XmlNode insertChild(int index, XmlNode child) {
        if (child.parent != null) {
            child.parent.children.remove(child);
        }
        child.parent = this;
        children.add(index, child);
        return this;
    }

    /**
     * Removes a child node.
     *
     * @param child the child to remove
     * @return true if the child was found and removed
     */
    public boolean removeChild(XmlNode child) {
        if (children.remove(child)) {
            child.parent = null;
            return true;
        }
        return false;
    }

    /**
     * Removes the child at the given index.
     *
     * @param index the index
     * @return the removed child
     */
    public XmlNode removeChild(int index) {
        XmlNode child = children.remove(index);
        if (child != null) {
            child.parent = null;
        }
        return child;
    }

    /**
     * @param index the index
     * @return the child node at the given index
     */
    public XmlNode getChild(int index) {
        return children.get(index);
    }

    /**
     * @return an unmodifiable view of the children
     */
    public List<XmlNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    /**
     * @return the number of child nodes
     */
    public int childCount() {
        return children.size();
    }

    /**
     * @return true if this node has no children
     */
    public boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * Finds the first child with the given tag name.
     *
     * @param tagName the tag name to search for
     * @return the first matching child, or null if not found
     */
    public XmlNode getFirstChildByName(String tagName) {
        for (XmlNode child : children) {
            if (child.getName().equals(tagName)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Finds all children with the given tag name.
     *
     * @param tagName the tag name to search for
     * @return a list of matching children
     */
    public List<XmlNode> getChildrenByName(String tagName) {
        List<XmlNode> result = new ArrayList<>();
        for (XmlNode child : children) {
            if (child.getName().equals(tagName)) {
                result.add(child);
            }
        }
        return result;
    }

    /**
     * Removes all children.
     */
    public void clearChildren() {
        for (XmlNode child : children) {
            child.parent = null;
        }
        children.clear();
    }

    // ============ text content ============

    /**
     * @return the text content, or null if not set
     */
    public String getTextContent() {
        return textContent;
    }

    /**
     * Sets the text content. If the node has children, the text content
     * replaces all children.
     *
     * @param textContent the text content, or null to clear
     */
    public void setTextContent(String textContent) {
        this.textContent = textContent;
        if (textContent != null) {
            children.clear();
        }
    }

    /**
     * @return true if this node has text content
     */
    public boolean hasTextContent() {
        return textContent != null && !textContent.isEmpty();
    }

    // ============ parent ============

    /**
     * @return the parent node, or null if this is the root
     */
    public XmlNode getParent() {
        return parent;
    }

    /**
     * @return true if this node has no parent
     */
    public boolean isRoot() {
        return parent == null;
    }

    // ============ query ============

    /**
     * @return true if this node is a leaf (no children, no text content)
     */
    public boolean isLeaf() {
        return children.isEmpty() && (textContent == null || textContent.isEmpty());
    }

    /**
     * @return true if this node has only text content and no children
     */
    public boolean isTextOnly() {
        return children.isEmpty() && textContent != null;
    }

    /**
     * @return true if this node has only child elements and no text content
     */
    public boolean isElementOnly() {
        return !children.isEmpty() && (textContent == null || textContent.isEmpty());
    }

    /**
     * Recursively finds all descendant nodes with the given tag name.
     *
     * @param tagName the tag name to search for
     * @return a list of matching descendant nodes
     */
    public List<XmlNode> findDescendants(String tagName) {
        List<XmlNode> result = new ArrayList<>();
        findDescendantsRecursive(tagName, result);
        return result;
    }

    private void findDescendantsRecursive(String tagName, List<XmlNode> result) {
        for (XmlNode child : children) {
            if (child.getName().equals(tagName)) {
                result.add(child);
            }
            child.findDescendantsRecursive(tagName, result);
        }
    }

    // ============ iteration ============

    @Override
    public Iterator<XmlNode> iterator() {
        return children.iterator();
    }

    // ============ equals / hashCode ============

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XmlNode xmlNode = (XmlNode) o;
        return name.equals(xmlNode.name)
                && attributes.equals(xmlNode.attributes)
                && children.equals(xmlNode.children)
                && Objects.equals(textContent, xmlNode.textContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, attributes, children, textContent);
    }

    @Override
    public String toString() {
        return "XmlNode{" +
                "name='" + name + '\'' +
                ", attributes=" + attributes +
                ", children=" + children.size() +
                ", textContent='" + (textContent != null ? textContent : "") + '\'' +
                '}';
    }
}