/**
 * Copyright (c) 2020 TruthBean(Rogar·Q)
 * Debbie is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *         http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */
package com.truthbean.debbie.net.uri;

import com.truthbean.debbie.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * uri composition:
 * [scheme]://[username]:[password]@[host]:[port]/[path1/path2/.../path][.suffix];[matrix1=value&amp;matrix1=value]?[query1=value&amp;query2=value]#[hashPath/hashPath;hashMatrix=value?hashQuery=value]
 * @see <a href="https://zh.wikipedia.org/wiki/%E7%BB%9F%E4%B8%80%E8%B5%84%E6%BA%90%E6%A0%87%E5%BF%97%E7%AC%A6">uri</a>
 *
 * @author 璩诗斌
 * @since 0.0.1
 */
public class UriComposition {
    private String scheme;
    private String username;
    private String password;
    private String host;
    private int port = 80;
    private List<String> paths;
    private String suffix;
    private Map<String, List<String>> matrix;
    private Map<String, List<String>> queries;
    private UriFragmentComposition fragment;

    public String getScheme() {
        return scheme;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public List<String> getPaths() {
        return paths;
    }

    public String getSuffix() {
        return suffix;
    }

    public Map<String, List<String>> getMatrix() {
        return matrix;
    }

    public Map<String, List<String>> getQueries() {
        return queries;
    }

    public UriFragmentComposition getFragment() {
        return fragment;
    }

    public static class Builder {
        private final UriComposition uriComposition;

        public Builder() {
            uriComposition = new UriComposition();
        }

        public Builder scheme(String scheme) {
            if (scheme != null && !scheme.isBlank()) {
                uriComposition.scheme = scheme.trim();
            }
            return this;
        }

        public Builder username(String username) {
            if (username != null && !username.isBlank()) {
                uriComposition.username = username;
            }
            return this;
        }

        public Builder password(String password) {
            uriComposition.password = password;
            return this;
        }

        public Builder host(String host) {
            if (host != null && !host.isBlank()) {
                uriComposition.host = host;
            }
            return this;
        }

        public Builder port(int port) {
            if (port > 0) {
                uriComposition.port = port;
            } else {
                uriComposition.port = 80;
            }
            return this;
        }

        public Builder paths(List<String> paths) {
            if (paths != null && !paths.isEmpty()) {
                uriComposition.paths = paths;
            } else {
                uriComposition.paths = new ArrayList<>();
            }
            return this;
        }

        public Builder addPath(String path) {
            if (uriComposition.paths == null) {
                uriComposition.paths = new ArrayList<>();
            }
            if (path != null && !path.isBlank()) {
                uriComposition.paths.add(path);
            }
            return this;
        }

        public Builder suffix(String suffix) {
            if (suffix != null) {
                uriComposition.suffix = suffix;
            }
            return this;
        }

        public Builder matrix(Map<String, List<String>> matrix) {
            if (uriComposition.matrix == null) {
                uriComposition.matrix = new HashMap<>();
            }
            if (matrix != null) {
                uriComposition.matrix.putAll(matrix);
            }
            return this;
        }

        public Builder addMatrix(Map<String, List<String>> matrix) {
            if (uriComposition.matrix == null) {
                uriComposition.matrix = new HashMap<>();
            }
            if (matrix != null) {
                matrix.forEach((k, v) -> {
                    List<String> values;
                    if (uriComposition.matrix.containsKey(k)) {
                        values = uriComposition.matrix.get(k);
                    } else {
                        values = new ArrayList<>();
                    }
                    if (v != null && !v.isEmpty()) {
                        values.addAll(v);
                    }
                    uriComposition.matrix.put(k, values);
                });
            }
            return this;
        }

        public Builder addMatrix(String name, List value) {
            if (uriComposition.matrix == null) {
                uriComposition.matrix = new HashMap<>();
            }
            if (value != null && !value.isEmpty()) {
                List<String> newMatrixValue = new ArrayList<>();
                for (var m : value) {
                    newMatrixValue.add(m.toString());
                }
                uriComposition.matrix.put(name, newMatrixValue);
            }
            return this;
        }

        public Builder addMatrix(String name, Object value) {
            if (uriComposition.matrix == null) {
                uriComposition.matrix = new HashMap<>();
            }
            if (value != null) {
                List<String> newMatrixValue = new ArrayList<>();
                newMatrixValue.add(value.toString());
                uriComposition.matrix.put(name, newMatrixValue);
            }
            return this;
        }

        public Builder queries(Map<String, List<String>> queries) {
            if (uriComposition.queries != null && queries != null) {
                uriComposition.queries.putAll(queries);
            } else {
                uriComposition.queries = queries;
            }
            return this;
        }

        public Builder addQueries(String name, List value) {
            if (uriComposition.queries == null) {
                uriComposition.queries = new HashMap<>();
            }

            if (value != null && !value.isEmpty()) {
                List<String> newValue = new ArrayList<>();
                for (var m : value) {
                    newValue.add(m.toString());
                }
                uriComposition.queries.put(name, newValue);
            }
            return this;
        }

        public Builder addQueries(String name, Object value) {
            if (uriComposition.queries == null) {
                uriComposition.queries = new HashMap<>();
            }

            if (value != null) {
                List<String> newValue = new ArrayList<>();
                newValue.add(value.toString());
                uriComposition.queries.put(name, newValue);
            }
            return this;
        }

        public Builder fragment(UriFragmentComposition fragment) {
            uriComposition.fragment = fragment;
            return this;
        }

        public UriComposition build() {
            return uriComposition;
        }

        private String toUri() {
            StringBuilder uriBuilder = new StringBuilder();
            uriBuilder.append(uriComposition.scheme).append("://");

            if (uriComposition.username != null) {
                uriBuilder.append(uriComposition.username).append(":");
                var password = uriComposition.password;
                if (password == null) {
                    password = "";
                }
                uriBuilder.append(password).append("@");
            }

            uriBuilder.append(uriComposition.host);
            if (uriComposition.port != 80) {
                uriBuilder.append(":").append(uriComposition.port);
            }
            uriBuilder.append("/");
            var paths = uriComposition.paths;
            if (paths != null && !paths.isEmpty()) {
                for (String path : paths) {
                    uriBuilder.append(path).append("/");
                }
            }

            uriBuilder.append(".").append(uriComposition.suffix);

            var matrix = uriComposition.matrix;
            StringUtils.joining(matrix, ";", "=", ",", uriBuilder);

            var queries = uriComposition.queries;
            StringUtils.joiningWithMultiKey(queries, "&", "=", uriBuilder);

            uriBuilder.append(uriComposition.fragment.toFragment());

            return uriBuilder.toString();
        }

    }

    @Override
    public String toString() {
        return "{" +
                "\"scheme\":\"" + scheme + '\"' +
                ",\"username\":\"" + username + '\"' +
                ",\"password\":\"" + password + '\"' +
                ",\"host\":\"" + host + '\"' +
                ",\"port\":" + port +
                ",\"paths\":" + paths +
                ",\"suffix\":\"" + suffix + '\"' +
                ",\"matrix\":" + matrix +
                ",\"queries\":" + queries +
                ",\"fragment\":" + fragment +
                '}';
    }
}
