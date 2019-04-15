package com.truthbean.debbie.core.net.uri;

import com.truthbean.debbie.core.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 璩诗斌
 * @since 0.0.1
 */
public class UriComposition {
    private String scheme;
    private String host;
    private int port = 80;
    private List<String> fragments;
    private String suffix;
    private Map<String, List<String>> matrix;
    private Map<String, List<String>> queries;

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<String> getFragments() {
        return fragments;
    }

    public void setFragments(List<String> fragments) {
        this.fragments = fragments;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Map<String, List<String>> getMatrix() {
        return matrix;
    }

    public void setMatrix(Map<String, List<String>> matrix) {
        this.matrix = matrix;
    }

    public Map<String, List<String>> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, List<String>> queries) {
        this.queries = queries;
    }

    public static class Builder {
        private UriComposition uriComposition;

        public Builder() {
            uriComposition = new UriComposition();
        }

        public Builder scheme(String scheme) {
            uriComposition.scheme = scheme;
            return this;
        }

        public Builder host(String host) {
            uriComposition.host = host;
            return this;
        }

        public Builder port(int port) {
            uriComposition.port = port;
            return this;
        }

        public Builder fragments(List<String> fragments) {
            uriComposition.fragments = fragments;
            return this;
        }

        public Builder suffix(String suffix) {
            uriComposition.suffix = suffix;
            return this;
        }

        private Builder matrix(Map<String, List<String>> matrix) {
            if (uriComposition.matrix != null && matrix != null) {
                uriComposition.matrix.putAll(matrix);
            } else {
                uriComposition.matrix = matrix;
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

        public UriComposition build() {
            return uriComposition;
        }

        private String toUri() {
            StringBuilder uriBuilder = new StringBuilder();
            uriBuilder.append(uriComposition.scheme).append("://")
                    .append(uriComposition.host);
            if (uriComposition.port != 80) {
                uriBuilder.append(":").append(uriComposition.port);
            }
            uriBuilder.append("/");
            var fragments = uriComposition.fragments;
            if (fragments != null && !fragments.isEmpty()) {
                for (String fragment : fragments) {
                    uriBuilder.append(fragment).append("/");
                }
            }

            var matrix = uriComposition.matrix;
            StringUtils.joining(matrix, ";", "=", ",", uriBuilder);

            if (matrix == null) {
                uriBuilder.append(".").append(uriComposition.suffix);
            }


            var queries = uriComposition.queries;
            StringUtils.joiningWithMultiKey(queries, "&", "=", uriBuilder);

            return uriBuilder.toString();
        }

    }
}
