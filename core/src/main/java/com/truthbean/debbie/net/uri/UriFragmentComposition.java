/**
 * Copyright (c) 2022 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.net.uri;

import com.truthbean.common.mini.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * uri fragment
 * [path1/path2/.../path];[matrix1=VALUE&amp;matrix1=VALUE]?[query1=VALUE&amp;query2=VALUE]
 *
 * @author 璩诗斌
 * @since 0.0.1
 */
public class UriFragmentComposition {
    private List<String> paths;
    private Map<String, List<String>> matrix;
    private Map<String, List<String>> queries;

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public void addPaths(List<String> paths) {
        if (this.paths == null) {
            this.paths = new ArrayList<>();
        }
        this.paths.addAll(paths);
    }

    public void addPath(String path) {
        if (this.paths == null) {
            this.paths = new ArrayList<>();
        }
        this.paths.add(path);
    }

    public Map<String, List<String>> getMatrix() {
        return matrix;
    }

    public void setMatrix(Map<String, List<String>> matrix) {
        this.matrix = matrix;
    }

    public void addMatrix(Map<String, List<String>> matrix) {
        if (this.matrix == null) {
            this.matrix = new HashMap<>();
        }
        this.matrix.putAll(matrix);
    }

    public void addMatrix(String name, List<String> value) {
        if (this.matrix == null) {
            this.matrix = new HashMap<>();
        }
        this.matrix.put(name, value);
    }

    public void addMatrix(String name, String value) {
        if (this.matrix == null) {
            this.matrix = new HashMap<>();
        }
        if (this.matrix.containsKey(name)) {
            List<String> values = this.matrix.get(name);
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(value);
            this.matrix.put(name, values);
        }
        List<String> values = new ArrayList<>();
        values.add(value);
        this.matrix.put(name, values);
    }

    public Map<String, List<String>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, List<String>> queries) {
        this.queries = queries;
    }

    public void addQueries(Map<String, List<String>> queries) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        this.queries.putAll(queries);
    }

    public void addQuery(String name, List<String> value) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        this.queries.put(name, value);
    }

    public void addQuery(String name, String value) {
        if (this.queries == null) {
            this.queries = new HashMap<>();
        }
        if (this.queries.containsKey(name)) {
            List<String> values = this.queries.get(name);
            if (values == null) {
                values = new ArrayList<>();
            }
            values.add(value);
            this.queries.put(name, values);
        }
        List<String> values = new ArrayList<>();
        values.add(value);
        this.queries.put(name, values);
    }

    @Override
    public String toString() {
        return "{" +
                "\"paths\":" + paths +
                ",\"matrix\":" + matrix +
                ",\"queries\":" + queries +
                '}';
    }

    public String toFragment() {
        StringBuilder uriBuilder = new StringBuilder();

        uriBuilder.append("/");
        if (paths != null && !paths.isEmpty()) {
            for (String path : paths) {
                uriBuilder.append(path).append("/");
            }
        }

        StringUtils.joining(matrix, ";", "=", ",", uriBuilder);

        StringUtils.joiningWithMultiKey(queries, "&", "=", uriBuilder);

        return uriBuilder.toString();
    }
}
