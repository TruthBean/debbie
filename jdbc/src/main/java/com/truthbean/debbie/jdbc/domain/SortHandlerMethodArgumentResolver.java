package com.truthbean.debbie.jdbc.domain;

import com.truthbean.debbie.data.validate.DataValidateFactory;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentResolver;
import com.truthbean.debbie.util.Assert;
import com.truthbean.debbie.util.StringUtils;

import java.util.*;

/**
 * {@link ExecutableArgumentResolver} to automatically create {@link Sort} instances from request parameters or
 * {@link SortDefault} annotations.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 * @author Nick Williams
 * @author Mark Paluch
 * @author Christoph Strobl
 * @since 1.6
 */
public class SortHandlerMethodArgumentResolver implements ExecutableArgumentResolver {

    private static final String DEFAULT_PARAMETER = "sort";
    private static final String DEFAULT_PROPERTY_DELIMITER = ",";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final Sort DEFAULT_SORT = Sort.unsorted();

    private static final String SORT_DEFAULTS_NAME = SortDefault.SortDefaults.class.getSimpleName();
    private static final String SORT_DEFAULT_NAME = SortDefault.class.getSimpleName();

    private Sort fallbackSort = DEFAULT_SORT;
    private String sortParameter = DEFAULT_PARAMETER;
    private String propertyDelimiter = DEFAULT_PROPERTY_DELIMITER;
    private String qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;

    /**
     * Configure the request parameter to lookup sort information from. Defaults to {@code sort}.
     *
     * @param sortParameter must not be {@literal null} or empty.
     */
    public void setSortParameter(String sortParameter) {

        Assert.hasText(sortParameter, "SortParameter must not be null nor empty!");
        this.sortParameter = sortParameter;
    }

    /**
     * Configures the delimiter used to separate property references and the direction to be sorted by. Defaults to
     * {@code}, which means sort values look like this: {@code firstname,lastname,asc}.
     *
     * @param propertyDelimiter must not be {@literal null} or empty.
     */
    public void setPropertyDelimiter(String propertyDelimiter) {

        Assert.hasText(propertyDelimiter, "Property delimiter must not be null or empty!");
        this.propertyDelimiter = propertyDelimiter;
    }

    /**
     * Configures the delimiter used to separate the qualifier from the sort parameter. Defaults to {@code _}, so a
     * qualified sort property would look like {@code qualifier_sort}.
     *
     * @param qualifierDelimiter the qualifier delimiter to be used or {@literal null} to reset to the default.
     */
    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter == null ? DEFAULT_QUALIFIER_DELIMITER : qualifierDelimiter;
    }

    @Override
    public boolean supportsParameter(ExecutableArgument parameter) {
        var annotation = parameter.getAnnotation(SortDefault.class);
        return annotation != null;
    }

    @Override
    public boolean resolveArgument(ExecutableArgument parameter, Object originValues, DataValidateFactory validateFactory) {
        Sort sort = getSort(parameter, originValues, validateFactory);
        parameter.setValue(sort);
        return true;
    }

    Sort getSort(ExecutableArgument parameter, Object originValues, DataValidateFactory validateFactory) {
        Map<String, List> map = (Map<String, List>) originValues;

        List<String> directionParameter = getParameterValues(map, getSortParameter(parameter));

        // No parameter
        if (directionParameter == null) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }

        // Single empty parameter, e.g "sort="
        if (directionParameter.size() == 1 && StringUtils.isBlank(directionParameter.get(0))) {
            return getDefaultFromAnnotationOrFallback(parameter);
        }

        return parseParameterIntoSort(directionParameter, propertyDelimiter);
    }

    private List getParameterValues(Map<String, List> map, String name) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, List> entry : map.entrySet()) {
                var key = entry.getKey();
                if (name.equals(key)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Reads the default {@link Sort} to be used from the given {@link ExecutableArgument}. Rejects the parameter if both an
     * {@link SortDefault.SortDefaults} and {@link SortDefault} annotation is found as we cannot build a reliable {@link Sort}
     * instance then (property ordering).
     *
     * @param parameter will never be {@literal null}.
     * @return the default {@link Sort} instance derived from the parameter annotations or the configured fallback-sort
     * {@link #setFallbackSort(Sort)}.
     */
    private Sort getDefaultFromAnnotationOrFallback(ExecutableArgument parameter) {

        SortDefault.SortDefaults annotatedDefaults = (SortDefault.SortDefaults) parameter.getAnnotation(SortDefault.SortDefaults.class);
        SortDefault annotatedDefault = (SortDefault) parameter.getAnnotation(SortDefault.class);

        if (annotatedDefault != null && annotatedDefaults != null) {
            throw new IllegalArgumentException(
                    String.format("Cannot use both @%s and @%s on parameter %s! Move %s into %s to define sorting order!",
                            SORT_DEFAULTS_NAME, SORT_DEFAULT_NAME, parameter.toString(), SORT_DEFAULT_NAME, SORT_DEFAULTS_NAME));
        }

        if (annotatedDefault != null) {
            return appendOrCreateSortTo(annotatedDefault, Sort.unsorted());
        }

        if (annotatedDefaults != null) {

            Sort sort = Sort.unsorted();

            for (SortDefault currentAnnotatedDefault : annotatedDefaults.value()) {
                sort = appendOrCreateSortTo(currentAnnotatedDefault, sort);
            }

            return sort;
        }

        return fallbackSort;
    }

    /**
     * Creates a new {@link Sort} instance from the given {@link SortDefault} or appends it to the given {@link Sort}
     * instance if it's not {@literal null}.
     *
     * @param sortDefault
     * @param sortOrNull
     * @return
     */
    private Sort appendOrCreateSortTo(SortDefault sortDefault, Sort sortOrNull) {

        String[] fields = sortDefault.sort();

        if (fields.length == 0) {
            return Sort.unsorted();
        }

        return sortOrNull.and(Sort.by(sortDefault.direction(), fields));
    }

    /**
     * Returns the sort parameter to be looked up from the request. Potentially applies qualifiers to it.
     *
     * @param parameter can be {@literal null}.
     * @return
     */
    protected String getSortParameter(ExecutableArgument parameter) {
        return sortParameter;
    }

    /**
     * Parses the given sort expressions into a {@link Sort} instance. The implementation expects the sources to be a
     * concatenation of Strings using the given delimiter. If the last element can be parsed into a {@link Sort.Direction} it's
     * considered a {@link Sort.Direction} and a simple property otherwise.
     *
     * @param source    will never be {@literal null}.
     * @param delimiter the delimiter to be used to split up the source elements, will never be {@literal null}.
     * @return
     */
    Sort parseParameterIntoSort(List<String> source, String delimiter) {

        List<Sort.Order> allOrders = new ArrayList<>();

        for (String part : source) {

            if (part == null) {
                continue;
            }

            String[] elements = part.split(delimiter);

            Optional<Sort.Direction> direction = elements.length == 0 ? Optional.empty()
                    : Sort.Direction.fromOptionalString(elements[elements.length - 1]);

            int lastIndex = direction.map(it -> elements.length - 1).orElseGet(() -> elements.length);

            for (int i = 0; i < lastIndex; i++) {
                toOrder(elements[i], direction).ifPresent(allOrders::add);
            }
        }

        return allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
    }

    private static Optional<Sort.Order> toOrder(String property, Optional<Sort.Direction> direction) {

        if (StringUtils.isBlank(property)) {
            return Optional.empty();
        }

        return Optional.of(direction.map(it -> new Sort.Order(it, property)).orElseGet(() -> Sort.Order.by(property)));
    }

    /**
     * Folds the given {@link Sort} instance into a {@link List} of sort expressions, accumulating {@link Sort.Order} instances
     * of the same direction into a single expression if they are in order.
     *
     * @param sort must not be {@literal null}.
     * @return
     */
    protected List<String> foldIntoExpressions(Sort sort) {

        List<String> expressions = new ArrayList<>();
        ExpressionBuilder builder = null;

        for (Sort.Order order : sort) {

            Sort.Direction direction = order.getDirection();

            if (builder == null) {
                builder = new ExpressionBuilder(direction);
            } else if (!builder.hasSameDirectionAs(order)) {
                builder.dumpExpressionIfPresentInto(expressions);
                builder = new ExpressionBuilder(direction);
            }

            builder.add(order.getProperty());
        }

        return builder == null ? Collections.emptyList() : builder.dumpExpressionIfPresentInto(expressions);
    }

    /**
     * Folds the given {@link Sort} instance into two expressions. The first being the property list, the second being the
     * direction.
     *
     * @param sort must not be {@literal null}.
     * @return
     * @throws IllegalArgumentException if a {@link Sort} with multiple {@link Sort.Direction}s has been handed in.
     */
    protected List<String> legacyFoldExpressions(Sort sort) {

        List<String> expressions = new ArrayList<>();
        ExpressionBuilder builder = null;

        for (Sort.Order order : sort) {

            Sort.Direction direction = order.getDirection();

            if (builder == null) {
                builder = new ExpressionBuilder(direction);
            } else if (!builder.hasSameDirectionAs(order)) {
                throw new IllegalArgumentException(String.format(
                        "%s in legacy configuration only supports a single direction to sort by!", getClass().getSimpleName()));
            }

            builder.add(order.getProperty());
        }

        return builder == null ? Collections.emptyList() : builder.dumpExpressionIfPresentInto(expressions);
    }

    /**
     * Helper to easily build request parameter expressions for {@link Sort} instances.
     *
     * @author Oliver Gierke
     */
    class ExpressionBuilder {

        private final List<String> elements = new ArrayList<>();
        private final Sort.Direction direction;

        /**
         * Sets up a new {@link ExpressionBuilder} for properties to be sorted in the given {@link Sort.Direction}.
         *
         * @param direction must not be {@literal null}.
         */
        public ExpressionBuilder(Sort.Direction direction) {

            Assert.notNull(direction, "Direction must not be null!");
            this.direction = direction;
        }

        /**
         * Returns whether the given {@link Sort.Order} has the same direction as the current {@link ExpressionBuilder}.
         *
         * @param order must not be {@literal null}.
         * @return
         */
        public boolean hasSameDirectionAs(Sort.Order order) {
            return this.direction == order.getDirection();
        }

        /**
         * Adds the given property to the expression to be built.
         *
         * @param property
         */
        public void add(String property) {
            this.elements.add(property);
        }

        /**
         * Dumps the expression currently in build into the given {@link List} of {@link String}s. Will only dump it in case
         * there are properties piled up currently.
         *
         * @param expressions
         * @return
         */
        public List<String> dumpExpressionIfPresentInto(List<String> expressions) {

            if (elements.isEmpty()) {
                return expressions;
            }

            elements.add(direction.name().toLowerCase());
            expressions.add(StringUtils.joining(elements, propertyDelimiter));

            return expressions;
        }
    }

    /**
     * Configures the {@link Sort} to be used as fallback in case no {@link SortDefault} or {@link SortDefault.SortDefaults} (the
     * latter only supported in legacy mode) can be found at the method parameter to be resolved.
     * <p>
     * If you set this to {@literal null}, be aware that you controller methods will get {@literal null} handed into them
     * in case no {@link Sort} data can be found in the request.
     *
     * @param fallbackSort the {@link Sort} to be used as general fallback.
     */
    public void setFallbackSort(Sort fallbackSort) {
        this.fallbackSort = fallbackSort;
    }
}