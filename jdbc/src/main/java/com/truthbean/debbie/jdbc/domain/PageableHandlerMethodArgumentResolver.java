package com.truthbean.debbie.jdbc.domain;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.truthbean.debbie.data.validate.DataValidateFactory;
import com.truthbean.debbie.reflection.ExecutableArgument;
import com.truthbean.debbie.reflection.ExecutableArgumentResolver;
import com.truthbean.debbie.util.Assert;
import com.truthbean.debbie.util.StringUtils;

/**
 * Extracts paging information from web requests and thus allows injecting {@link Pageable} instances into controller
 * methods. Request properties to be parsed can be configured. Default configuration uses request parameters beginning
 * with {@link #DEFAULT_PAGE_PARAMETER}{@link #DEFAULT_QUALIFIER_DELIMITER}.
 *
 * @author Oliver Gierke
 * @author Nick Williams
 * @author Mark Paluch
 * @author Christoph Strobl
 * @since 1.6
 */
public class PageableHandlerMethodArgumentResolver implements ExecutableArgumentResolver {

    private static final SortHandlerMethodArgumentResolver DEFAULT_SORT_RESOLVER = new SortHandlerMethodArgumentResolver();
    private static final String INVALID_DEFAULT_PAGE_SIZE = "Invalid default page size configured for method %s! Must not be less than one!";

    private static final String DEFAULT_PAGE_PARAMETER = "page";
    private static final String DEFAULT_SIZE_PARAMETER = "size";
    private static final String DEFAULT_PREFIX = "";
    private static final String DEFAULT_QUALIFIER_DELIMITER = "_";
    private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
    static final PageRequest DEFAULT_PAGE_REQUEST = PageRequest.of(0, 20);

    private PageRequest fallbackPageable = DEFAULT_PAGE_REQUEST;
    private SortHandlerMethodArgumentResolver sortResolver;
    private String pageParameterName = DEFAULT_PAGE_PARAMETER;
    private String sizeParameterName = DEFAULT_SIZE_PARAMETER;
    private String prefix = DEFAULT_PREFIX;
    private String qualifierDelimiter = DEFAULT_QUALIFIER_DELIMITER;
    private int maxPageSize = DEFAULT_MAX_PAGE_SIZE;
    private boolean oneIndexedParameters = false;

    /**
     * Constructs an instance of this resolved with a default {@link SortHandlerMethodArgumentResolver}.
     */
    public PageableHandlerMethodArgumentResolver() {
        this((SortHandlerMethodArgumentResolver) null);
    }

    /**
     * Constructs an instance of this resolver with the specified {@link SortHandlerMethodArgumentResolver}.
     *
     * @param sortResolver the sort resolver to use
     * @since 1.13
     */
    public PageableHandlerMethodArgumentResolver(SortHandlerMethodArgumentResolver sortResolver) {
        this.sortResolver = sortResolver == null ? DEFAULT_SORT_RESOLVER : sortResolver;
    }

    /**
     * Configures the {@link Pageable} to be used as fallback in case no {@link PageableDefault} or
     * {@link PageableDefault} (the latter only supported in legacy mode) can be found at the method parameter to be
     * resolved.
     * <p>
     * If you set this to {@literal Optional#empty()}, be aware that you controller methods will get {@literal null}
     * handed into them in case no {@link Pageable} data can be found in the request. Note, that doing so will require you
     * supply bot the page <em>and</em> the size parameter with the requests as there will be no default for any of the
     * parameters available.
     *
     * @param fallbackPageable the {@link Pageable} to be used as general fallback.
     */
    public void setFallbackPageable(PageRequest fallbackPageable) {

        Assert.notNull(fallbackPageable, "Fallback Pageable must not be null!");

        this.fallbackPageable = fallbackPageable;
    }

    /**
     * Returns whether the given {@link Pageable} is the fallback one.
     *
     * @param pageable can be {@literal null}.
     * @return
     * @since 1.9
     */
    public boolean isFallbackPageable(PageRequest pageable) {
        return fallbackPageable != null && fallbackPageable.equals(pageable);
    }

    /**
     * Configures the maximum page size to be accepted. This allows to put an upper boundary of the page size to prevent
     * potential attacks trying to issue an {@link OutOfMemoryError}. Defaults to {@link #DEFAULT_MAX_PAGE_SIZE}.
     *
     * @param maxPageSize the maxPageSize to set
     */
    public void setMaxPageSize(int maxPageSize) {
        this.maxPageSize = maxPageSize;
    }

    /**
     * Retrieves the maximum page size to be accepted. This allows to put an upper boundary of the page size to prevent
     * potential attacks trying to issue an {@link OutOfMemoryError}. Defaults to {@link #DEFAULT_MAX_PAGE_SIZE}.
     *
     * @return the maximum page size allowed.
     */
    protected int getMaxPageSize() {
        return this.maxPageSize;
    }

    /**
     * Configures the parameter name to be used to find the page number in the request. Defaults to {@code page}.
     *
     * @param pageParameterName the parameter name to be used, must not be {@literal null} or empty.
     */
    public void setPageParameterName(String pageParameterName) {

        Assert.hasText(pageParameterName, "Page parameter name must not be null or empty!");
        this.pageParameterName = pageParameterName;
    }

    /**
     * Retrieves the parameter name to be used to find the page number in the request. Defaults to {@code page}.
     *
     * @return the parameter name to be used, never {@literal null} or empty.
     */
    protected String getPageParameterName() {
        return this.pageParameterName;
    }

    /**
     * Configures the parameter name to be used to find the page size in the request. Defaults to {@code size}.
     *
     * @param sizeParameterName the parameter name to be used, must not be {@literal null} or empty.
     */
    public void setSizeParameterName(String sizeParameterName) {

        Assert.hasText(sizeParameterName, "Size parameter name must not be null or empty!");
        this.sizeParameterName = sizeParameterName;
    }

    /**
     * Retrieves the parameter name to be used to find the page size in the request. Defaults to {@code size}.
     *
     * @return the parameter name to be used, never {@literal null} or empty.
     */
    protected String getSizeParameterName() {
        return this.sizeParameterName;
    }

    /**
     * Configures a general prefix to be prepended to the page number and page size parameters. Useful to namespace the
     * property names used in case they are clashing with ones used by your application. By default, no prefix is used.
     *
     * @param prefix the prefix to be used or {@literal null} to reset to the default.
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix == null ? DEFAULT_PREFIX : prefix;
    }

    /**
     * The delimiter to be used between the qualifier and the actual page number and size properties. Defaults to
     * {@code _}. So a qualifier of {@code foo} will result in a page number parameter of {@code foo_page}.
     *
     * @param qualifierDelimiter the delimiter to be used or {@literal null} to reset to the default.
     */
    public void setQualifierDelimiter(String qualifierDelimiter) {
        this.qualifierDelimiter = qualifierDelimiter == null ? DEFAULT_QUALIFIER_DELIMITER : qualifierDelimiter;
    }

    /**
     * Configures whether to expose and assume 1-based page number indexes in the request parameters. Defaults to
     * {@literal false}, meaning a page number of 0 in the request equals the first page. If this is set to
     * {@literal true}, a page number of 1 in the request will be considered the first page.
     *
     * @param oneIndexedParameters the oneIndexedParameters to set
     */
    public void setOneIndexedParameters(boolean oneIndexedParameters) {
        this.oneIndexedParameters = oneIndexedParameters;
    }

    /**
     * Indicates whether to expose and assume 1-based page number indexes in the request parameters. Defaults to
     * {@literal false}, meaning a page number of 0 in the request equals the first page. If this is set to
     * {@literal true}, a page number of 1 in the request will be considered the first page.
     *
     * @return whether to assume 1-based page number indexes in the request parameters.
     */
    protected boolean isOneIndexedParameters() {
        return this.oneIndexedParameters;
    }

    @Override
    public boolean supportsParameter(ExecutableArgument parameter) {
        return PageRequest.class.equals(parameter.getType());
    }

    public boolean resolveArgument(ExecutableArgument executableArgument, Object originValues, DataValidateFactory validateFactory) {
        Map<String, List> map = (Map<String, List>) originValues;
        PageRequest defaultOrFallback = getDefaultFromAnnotationOrFallback(executableArgument);

        String pageString = (String) getParameter(map, getParameterNameToUse(pageParameterName));
        String pageSizeString = (String) getParameter(map, getParameterNameToUse(sizeParameterName));

        Optional<Integer> page = parseAndApplyBoundaries(pageString, Integer.MAX_VALUE, true);
        Optional<Integer> pageSize = parseAndApplyBoundaries(pageSizeString, maxPageSize, false);

        if (!(page.isPresent() && pageSize.isPresent()) && defaultOrFallback != null) {
            executableArgument.setValue(defaultOrFallback);
            return true;
        }

        int p = page.orElse(defaultOrFallback.getCurrentPage());
        int ps = pageSize.orElse(defaultOrFallback.getPageSize());

        // Limit lower bound
        ps = ps < 1 ? defaultOrFallback.getPageSize() : ps;
        // Limit upper bound
        ps = ps > maxPageSize ? maxPageSize : ps;

        Sort sort = sortResolver.getSort(executableArgument, originValues, validateFactory);

        PageRequest pageRequest = PageRequest.of(p, ps, sort.isSorted() ? sort : defaultOrFallback.getSort());
        executableArgument.setValue(pageRequest);
        return true;
    }

    private Object getParameter(Map<String, List> map, String name) {
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, List> entry : map.entrySet()) {
                var key = entry.getKey();
                if (name.equals(key) ) {
                    return entry.getValue().get(0);
                }
            }
        }
        return null;
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
     * Returns the name of the request parameter to find the {@link Pageable} information in. Inspects the given
     * {@link ExecutableArgument} prefixes the given source parameter name with it.
     *
     * @param source    the basic parameter name.
     * @return the name of the request parameter.
     */
    protected String getParameterNameToUse(String source) {
        return prefix + source;
    }

    private PageRequest getDefaultFromAnnotationOrFallback(ExecutableArgument executableArgument) {
        PageableDefault defaults = (PageableDefault) executableArgument.getAnnotation(PageableDefault.class);
        if (defaults != null) {
            return getDefaultPageRequestFrom(executableArgument, defaults);
        }

        return fallbackPageable;
    }

    private static PageRequest getDefaultPageRequestFrom(ExecutableArgument parameter, PageableDefault defaults) {
        Integer defaultPageNumber = defaults.page();
        Integer defaultPageSize = defaults.size();

        if (defaultPageSize < 1) {
            throw new IllegalStateException(INVALID_DEFAULT_PAGE_SIZE);
        }

        if (defaults.sort().length == 0) {
            return PageRequest.of(defaultPageNumber, defaultPageSize);
        }

        return PageRequest.of(defaultPageNumber, defaultPageSize, defaults.direction(), defaults.sort());
    }

    /**
     * Tries to parse the given {@link String} into an integer and applies the given boundaries. Will return 0 if the
     * {@link String} cannot be parsed.
     *
     * @param parameter  the parameter value.
     * @param upper      the upper bound to be applied.
     * @param shiftIndex whether to shift the index if {@link #oneIndexedParameters} is set to true.
     * @return
     */
    private Optional<Integer> parseAndApplyBoundaries(String parameter, int upper, boolean shiftIndex) {

        if (StringUtils.isBlank(parameter)) {
            return Optional.empty();
        }

        try {
            int parsed = Integer.parseInt(parameter) - (oneIndexedParameters && shiftIndex ? 1 : 0);
            return Optional.of(parsed < 0 ? 0 : parsed > upper ? upper : parsed);
        } catch (NumberFormatException e) {
            return Optional.of(0);
        }
    }
}