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
import java.math.BigInteger;
import java.util.Objects;

/**
 * Represents a YAML scalar value: string, number, or boolean.
 *
 * @author TruthBean/Rogar·Q
 * @since 0.6.3
 */
public final class YamlScalar implements YamlNode {

    private final Object value;

    /**
     * @param value the string value, must not be null
     */
    public YamlScalar(String value) {
        this.value = Objects.requireNonNull(value, "string value must not be null");
    }

    /**
     * @param value the number value, must not be null
     */
    public YamlScalar(Number value) {
        this.value = Objects.requireNonNull(value, "number value must not be null");
    }

    /**
     * @param value the boolean value
     */
    public YamlScalar(Boolean value) {
        this.value = Objects.requireNonNull(value, "boolean value must not be null");
    }

    @Override
    public boolean isScalar() {
        return true;
    }

    @Override
    public YamlScalar asScalar() {
        return this;
    }

    /**
     * @return true if this scalar holds a string value
     */
    public boolean isString() {
        return value instanceof String;
    }

    /**
     * @return true if this scalar holds a number value
     */
    public boolean isNumber() {
        return value instanceof Number;
    }

    /**
     * @return true if this scalar holds a boolean value
     */
    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    /**
     * @return the string value, or the string representation of the value
     */
    public String getAsString() {
        if (value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    /**
     * @return the value as a Number
     * @throws ClassCastException if the value is not a Number
     */
    public Number getAsNumber() {
        return (Number) value;
    }

    /**
     * @return the value as a boolean
     * @throws ClassCastException if the value is not a Boolean
     */
    public Boolean getAsBoolean() {
        return (Boolean) value;
    }

    /**
     * @return the value as an int
     */
    public int getAsInt() {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(getAsString());
    }

    /**
     * @return the value as a long
     */
    public long getAsLong() {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(getAsString());
    }

    /**
     * @return the value as a double
     */
    public double getAsDouble() {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(getAsString());
    }

    /**
     * @return the value as a BigDecimal
     */
    public BigDecimal getAsBigDecimal() {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        }
        return new BigDecimal(value.toString());
    }

    /**
     * @return the value as a BigInteger
     */
    public BigInteger getAsBigInteger() {
        if (value instanceof BigInteger) {
            return (BigInteger) value;
        }
        return new BigInteger(value.toString());
    }

    /**
     * @return the raw value object
     */
    public Object getValue() {
        return value;
    }

    @Override
    public YamlNode deepCopy() {
        if (value instanceof String) {
            return new YamlScalar((String) value);
        }
        if (value instanceof Number) {
            return new YamlScalar((Number) value);
        }
        return new YamlScalar((Boolean) value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        YamlScalar that = (YamlScalar) o;
        if (value == null) return that.value == null;
        if (isNumber() && that.isNumber()) {
            double d1 = toNumber(value).doubleValue();
            double d2 = toNumber(that.value).doubleValue();
            return Double.compare(d1, d2) == 0;
        }
        return value.equals(that.value);
    }

    private static Number toNumber(Object obj) {
        if (obj instanceof Double || obj instanceof Float) {
            return ((Number) obj).doubleValue();
        }
        return ((Number) obj).longValue();
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return Objects.hashCode(value);
        }
        if (isNumber()) {
            double doubleValue = toNumber(value).doubleValue();
            return Objects.hash(doubleValue);
        }
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}