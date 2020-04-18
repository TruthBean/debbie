package com.truthbean.debbie.net.uri;

import com.truthbean.debbie.util.Assert;
import com.truthbean.debbie.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ant-style path patterns.
 *
 * &lt;p&gt;Part of this mapping code has been kindly borrowed from &lt;a href="http://ant.apache.org"&gt;Apache Ant&lt;/a&gt;.
 *
 * &lt;p&gt;The mapping matches URLs using the following rules:&lt;br&gt;
 * &lt;ul&gt;
 * &lt;li&gt;{@code ?} matches one character&lt;/li&gt;
 * &lt;li&gt;{@code *} matches zero or more characters&lt;/li&gt;
 * &lt;li&gt;{@code **} matches zero or more &lt;em&gt;directories&lt;/em&gt; in a path&lt;/li&gt;
 * &lt;li&gt;{@code {spring:[a-z]+}} matches the regexp {@code [a-z]+} as a path variable named "spring"&lt;/li&gt;
 * &lt;/ul&gt;
 *
 * &lt;h3&gt;Examples&lt;/h3&gt;
 * &lt;ul&gt;
 * &lt;li&gt;{@code com/t?st.jsp} &mdash; matches {@code com/test.jsp} but also
 * {@code com/tast.jsp} or {@code com/txst.jsp}&lt;/li&gt;
 * &lt;li&gt;{@code com/*.jsp} &mdash; matches all {@code .jsp} files in the
 * {@code com} directory&lt;/li&gt;
 * &lt;li&gt;&lt;code&gt;com/&#42;&#42;/test.jsp&lt;/code&gt; &mdash; matches all {@code test.jsp}
 * files underneath the {@code com} path&lt;/li&gt;
 * &lt;li&gt;&lt;code&gt;org/springframework/&#42;&#42;/*.jsp&lt;/code&gt; &mdash; matches all
 * {@code .jsp} files underneath the {@code org/springframework} path&lt;/li&gt;
 * &lt;li&gt;&lt;code&gt;org/&#42;&#42;/servlet/bla.jsp&lt;/code&gt; &mdash; matches
 * {@code org/springframework/servlet/bla.jsp} but also
 * {@code org/springframework/testing/servlet/bla.jsp} and {@code org/servlet/bla.jsp}&lt;/li&gt;
 * &lt;li&gt;{@code com/{filename:\\w+}.jsp} will match {@code com/test.jsp} and assign the value {@code test}
 * to the {@code filename} variable&lt;/li&gt;
 * &lt;/ul&gt;
 *
 * &lt;p&gt;&lt;strong&gt;Note:&lt;/strong&gt; a pattern and a path must both be absolute or must
 * both be relative in order for the two to match. Therefore it is recommended
 * that users of this implementation to sanitize patterns in order to prefix
 * them with "/" as it makes sense in the context in which they're used.
 *
 * @author Alef Arendsen
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 * @since 16.07.2003
 */
public class AntPathMatcher {

    /** Default path separator: "/". */
    public static final String DEFAULT_PATH_SEPARATOR = "/";

    private static final int CACHE_TURNOFF_THRESHOLD = 65536;

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");

    private static final char[] WILDCARD_CHARS = {'*', '?', '{'};


    private String pathSeparator;

    private PathSeparatorPatternCache pathSeparatorPatternCache;

    private boolean caseSensitive = true;

    private boolean trimTokens = false;

    private volatile Boolean cachePatterns;

    private final Map<String, String[]> tokenizedPatternCache = new ConcurrentHashMap<>(256);

    final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap<>(256);


    /**
     * Create a new instance with the {@link #DEFAULT_PATH_SEPARATOR}.
     */
    public AntPathMatcher() {
        this.pathSeparator = DEFAULT_PATH_SEPARATOR;
        this.pathSeparatorPatternCache = new PathSeparatorPatternCache(DEFAULT_PATH_SEPARATOR);
    }

    /**
     * A convenient, alternative constructor to use with a custom path separator.
     * @param pathSeparator the path separator to use, must not be {@code null}.
     * @since 4.1
     */
    public AntPathMatcher(String pathSeparator) {
        Assert.notNull(pathSeparator, "'pathSeparator' is required");
        this.pathSeparator = pathSeparator;
        this.pathSeparatorPatternCache = new PathSeparatorPatternCache(pathSeparator);
    }


    /**
     * Set the path separator to use for pattern parsing.
     * &lt;p&gt;Default is "/", as in Ant.
     * @param pathSeparator the path separator to use.
     */
    public void setPathSeparator(String pathSeparator) {
        this.pathSeparator = (pathSeparator != null ? pathSeparator : DEFAULT_PATH_SEPARATOR);
        this.pathSeparatorPatternCache = new PathSeparatorPatternCache(this.pathSeparator);
    }

    /**
     * Specify whether to perform pattern matching in a case-sensitive fashion.
     * &lt;p&gt;Default is {@code true}. Switch this to {@code false} for case-insensitive matching.
     *
     * @param caseSensitive is case sensitive
     * @since 4.2
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Specify whether to trim tokenized paths and patterns.
     * &lt;p&gt;Default is {@code false}.
     * @param trimTokens trim tokens
     */
    public void setTrimTokens(boolean trimTokens) {
        this.trimTokens = trimTokens;
    }

    /**
     * Specify whether to cache parsed pattern metadata for patterns passed
     * into this matcher's {@link #match} method. A value of {@code true}
     * activates an unlimited pattern cache; a value of {@code false} turns
     * the pattern cache off completely.
     * &lt;p&gt;Default is for the cache to be on, but with the variant to automatically
     * turn it off when encountering too many patterns to cache at runtime
     * (the threshold is 65536), assuming that arbitrary permutations of patterns
     * are coming in, with little chance for encountering a recurring pattern.
     *
     * @param cachePatterns cache patterns
     *
     * @since 4.0.1
     * @see #getStringMatcher(String)
     */
    public void setCachePatterns(boolean cachePatterns) {
        this.cachePatterns = cachePatterns;
    }

    private void deactivatePatternCache() {
        this.cachePatterns = false;
        this.tokenizedPatternCache.clear();
        this.stringMatcherCache.clear();
    }


    public boolean isPattern(String path) {
        return (path.indexOf('*') != -1 || path.indexOf('?') != -1);
    }

    public boolean match(String pattern, String path) {
        return doMatch(pattern, path, true, null);
    }

    public boolean matchStart(String pattern, String path) {
        return doMatch(pattern, path, false, null);
    }

    /**
     * Actually match the given {@code path} against the given {@code pattern}.
     * @param pattern the pattern to match against
     * @param path the path String to test
     * @param fullMatch whether a full pattern match is required (else a pattern match
     * @param uriTemplateVariables uri template variables
     * as far as the given base path goes is sufficient)
     * @return {@code true} if the supplied {@code path} matched, {@code false} if it didn't
     */
    protected boolean doMatch(String pattern, String path, boolean fullMatch,
                              Map<String, String> uriTemplateVariables) {

        if (path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
            return false;
        }

        String[] pattDirs = tokenizePattern(pattern);
        if (fullMatch && this.caseSensitive && !isPotentialMatch(path, pattDirs)) {
            return false;
        }

        String[] pathDirs = tokenizePath(path);

        int pattIdxStart = 0;
        int pattIdxEnd = pattDirs.length - 1;
        int pathIdxStart = 0;
        int pathIdxEnd = pathDirs.length - 1;

        // Match all elements up to the first **
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxStart];
            if ("**".equals(pattDir)) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxStart], uriTemplateVariables)) {
                return false;
            }
            pattIdxStart++;
            pathIdxStart++;
        }

        if (pathIdxStart > pathIdxEnd) {
            // Path is exhausted, only match if rest of pattern is * or **'s
            if (pattIdxStart > pattIdxEnd) {
                return (pattern.endsWith(this.pathSeparator) == path.endsWith(this.pathSeparator));
            }
            if (!fullMatch) {
                return true;
            }
            if (pattIdxStart == pattIdxEnd && "*".equals(pattDirs[pattIdxStart]) && path.endsWith(this.pathSeparator)) {
                return true;
            }
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!"**".equals(pattDirs[i])) {
                    return false;
                }
            }
            return true;
        } else if (pattIdxStart > pattIdxEnd) {
            // String not exhausted, but pattern is. Failure.
            return false;
        } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
            // Path start definitely matches due to "**" part in pattern.
            return true;
        }

        // up to last '**'
        while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            String pattDir = pattDirs[pattIdxEnd];
            if ("**".equals(pattDir)) {
                break;
            }
            if (!matchStrings(pattDir, pathDirs[pathIdxEnd], uriTemplateVariables)) {
                return false;
            }
            pattIdxEnd--;
            pathIdxEnd--;
        }
        if (pathIdxStart > pathIdxEnd) {
            // String is exhausted
            for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
                if (!"**".equals(pattDirs[i])) {
                    return false;
                }
            }
            return true;
        }

        while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
            int patIdxTmp = -1;
            for (int i = pattIdxStart + 1; i <= pattIdxEnd; i++) {
                if ("**".equals(pattDirs[i])) {
                    patIdxTmp = i;
                    break;
                }
            }
            if (patIdxTmp == pattIdxStart + 1) {
                // '**/**' situation, so skip one
                pattIdxStart++;
                continue;
            }
            // Find the pattern between padIdxStart & padIdxTmp in str between
            // strIdxStart & strIdxEnd
            int patLength = (patIdxTmp - pattIdxStart - 1);
            int strLength = (pathIdxEnd - pathIdxStart + 1);
            int foundIdx = -1;

            strLoop:
            for (int i = 0; i <= strLength - patLength; i++) {
                for (int j = 0; j < patLength; j++) {
                    String subPat = pattDirs[pattIdxStart + j + 1];
                    String subStr = pathDirs[pathIdxStart + i + j];
                    if (!matchStrings(subPat, subStr, uriTemplateVariables)) {
                        continue strLoop;
                    }
                }
                foundIdx = pathIdxStart + i;
                break;
            }

            if (foundIdx == -1) {
                return false;
            }

            pattIdxStart = patIdxTmp;
            pathIdxStart = foundIdx + patLength;
        }

        for (int i = pattIdxStart; i <= pattIdxEnd; i++) {
            if (!"**".equals(pattDirs[i])) {
                return false;
            }
        }

        return true;
    }

    private boolean isPotentialMatch(String path, String[] pattDirs) {
        if (!this.trimTokens) {
            int pos = 0;
            for (String pattDir : pattDirs) {
                int skipped = skipSeparator(path, pos, this.pathSeparator);
                pos += skipped;
                skipped = skipSegment(path, pos, pattDir);
                if (skipped < pattDir.length()) {
                    return (skipped > 0 || (pattDir.length() > 0 && isWildcardChar(pattDir.charAt(0))));
                }
                pos += skipped;
            }
        }
        return true;
    }

    private int skipSegment(String path, int pos, String prefix) {
        int skipped = 0;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (isWildcardChar(c)) {
                return skipped;
            }
            int currPos = pos + skipped;
            if (currPos >= path.length()) {
                return 0;
            }
            if (c == path.charAt(currPos)) {
                skipped++;
            }
        }
        return skipped;
    }

    private int skipSeparator(String path, int pos, String separator) {
        int skipped = 0;
        while (path.startsWith(separator, pos + skipped)) {
            skipped += separator.length();
        }
        return skipped;
    }

    private boolean isWildcardChar(char c) {
        for (char candidate : WILDCARD_CHARS) {
            if (c == candidate) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tokenize the given path pattern into parts, based on this matcher's settings.
     * &lt;p&gt;Performs caching based on {@link #setCachePatterns}, delegating to
     * {@link #tokenizePath(String)} for the actual tokenization algorithm.
     * @param pattern the pattern to tokenize
     * @return the tokenized pattern parts
     */
    protected String[] tokenizePattern(String pattern) {
        String[] tokenized = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns.booleanValue()) {
            tokenized = this.tokenizedPatternCache.get(pattern);
        }
        if (tokenized == null) {
            tokenized = tokenizePath(pattern);
            if (cachePatterns == null && this.tokenizedPatternCache.size() >= CACHE_TURNOFF_THRESHOLD) {
                // Try to adapt to the runtime situation that we're encountering:
                // There are obviously too many different patterns coming in here...
                // So let's turn off the cache since the patterns are unlikely to be reoccurring.
                deactivatePatternCache();
                return tokenized;
            }
            if (cachePatterns == null || cachePatterns.booleanValue()) {
                this.tokenizedPatternCache.put(pattern, tokenized);
            }
        }
        return tokenized;
    }

    /**
     * Tokenize the given path String into parts, based on this matcher's settings.
     * @param path the path to tokenize
     * @return the tokenized path parts
     */
    protected String[] tokenizePath(String path) {
        return StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
    }

    /**
     * Test whether or not a string matches against a pattern.
     * @param pattern the pattern to match against (never {@code null})
     * @param str the String which must be matched against the pattern (never {@code null})
     * @return {@code true} if the string matches against the pattern, or {@code false} otherwise
     */
    private boolean matchStrings(String pattern, String str,
                                 Map<String, String> uriTemplateVariables) {

        return getStringMatcher(pattern).matchStrings(str, uriTemplateVariables);
    }

    /**
     * Build or retrieve an {@link AntPathStringMatcher} for the given pattern.
     * &lt;p&gt;The default implementation checks this AntPathMatcher's internal cache
     * (see {@link #setCachePatterns}), creating a new AntPathStringMatcher instance
     * if no cached copy is found.
     * &lt;p&gt;When encountering too many patterns to cache at runtime (the threshold is 65536),
     * it turns the default cache off, assuming that arbitrary permutations of patterns
     * are coming in, with little chance for encountering a recurring pattern.
     * &lt;p&gt;This method may be overridden to implement a custom cache strategy.
     * @param pattern the pattern to match against (never {@code null})
     * @return a corresponding AntPathStringMatcher (never {@code null})
     * @see #setCachePatterns
     */
    protected AntPathStringMatcher getStringMatcher(String pattern) {
        AntPathStringMatcher matcher = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns.booleanValue()) {
            matcher = this.stringMatcherCache.get(pattern);
        }
        if (matcher == null) {
            matcher = new AntPathStringMatcher(pattern, this.caseSensitive);
            if (cachePatterns == null && this.stringMatcherCache.size() >= CACHE_TURNOFF_THRESHOLD) {
                // Try to adapt to the runtime situation that we're encountering:
                // There are obviously too many different patterns coming in here...
                // So let's turn off the cache since the patterns are unlikely to be reoccurring.
                deactivatePatternCache();
                return matcher;
            }
            if (cachePatterns == null || cachePatterns.booleanValue()) {
                this.stringMatcherCache.put(pattern, matcher);
            }
        }
        return matcher;
    }

    /**
     * Given a pattern and a full path, determine the pattern-mapped part.
     * @param path full path
     * For example: &lt;ul&gt;
     * &lt;li&gt;'{@code /docs/cvs/commit.html}' and '{@code /docs/cvs/commit.html} -&gt; ''&lt;/li&gt;
     * &lt;li&gt;'{@code /docs/*}' and '{@code /docs/cvs/commit} -&gt; '{@code cvs/commit}'&lt;/li&gt;
     * &lt;li&gt;'{@code /docs/cvs/*.html}' and '{@code /docs/cvs/commit.html} -&gt; '{@code commit.html}'&lt;/li&gt;
     * &lt;li&gt;'{@code /docs/**}' and '{@code /docs/cvs/commit} -&gt; '{@code cvs/commit}'&lt;/li&gt;
     * &lt;li&gt;'{@code /docs/**\/*.html}' and '{@code /docs/cvs/commit.html} -&gt; '{@code cvs/commit.html}'&lt;/li&gt;
     * &lt;li&gt;'{@code /*.html}' and '{@code /docs/cvs/commit.html} -&gt; '{@code docs/cvs/commit.html}'&lt;/li&gt;
     * &lt;li&gt;'{@code *.html}' and '{@code /docs/cvs/commit.html} -&gt; '{@code /docs/cvs/commit.html}'&lt;/li&gt;
     * &lt;li&gt;'{@code *}' and '{@code /docs/cvs/commit.html} -&gt; '{@code /docs/cvs/commit.html}'&lt;/li&gt; &lt;/ul&gt;
     * &lt;p&gt;Assumes that {@link #match} returns {@code true} for '{@code pattern}' and '{@code path}', but
     * does &lt;strong&gt;not&lt;/strong&gt; enforce this.
     * @param pattern pattern
     */
    public String extractPathWithinPattern(String pattern, String path) {
        String[] patternParts = StringUtils.tokenizeToStringArray(pattern, this.pathSeparator, this.trimTokens, true);
        String[] pathParts = StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
        StringBuilder builder = new StringBuilder();
        boolean pathStarted = false;

        for (int segment = 0; segment < patternParts.length; segment++) {
            String patternPart = patternParts[segment];
            if (patternPart.indexOf('*') > -1 || patternPart.indexOf('?') > -1) {
                for (; segment < pathParts.length; segment++) {
                    if (pathStarted || (segment == 0 && !pattern.startsWith(this.pathSeparator))) {
                        builder.append(this.pathSeparator);
                    }
                    builder.append(pathParts[segment]);
                    pathStarted = true;
                }
            }
        }

        return builder.toString();
    }

    public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
        Map<String, String> variables = new LinkedHashMap<>();
        boolean result = doMatch(pattern, path, true, variables);
        if (!result) {
            throw new IllegalStateException("Pattern \"" + pattern + "\" is not a match for \"" + path + "\"");
        }
        return variables;
    }

    /**
     * Combine two patterns into a new pattern.
     * &lt;p&gt;This implementation simply concatenates the two patterns, unless
     * the first pattern contains a file extension match (e.g., {@code *.html}).
     * In that case, the second pattern will be merged into the first. Otherwise,
     * an {@code IllegalArgumentException} will be thrown.
     * &lt;h3&gt;Examples&lt;/h3&gt;
     * &lt;table border="1"&gt;
     * &lt;tr&gt;&lt;th&gt;Pattern 1&lt;/th&gt;&lt;th&gt;Pattern 2&lt;/th&gt;&lt;th&gt;Result&lt;/th&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;{@code null}&lt;/td&gt;&lt;td&gt;{@code null}&lt;/td&gt;&lt;td&gt;&nbsp;&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;td&gt;{@code null}&lt;/td&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;{@code null}&lt;/td&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;td&gt;/bookings&lt;/td&gt;&lt;td&gt;/hotels/bookings&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;td&gt;bookings&lt;/td&gt;&lt;td&gt;/hotels/bookings&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels/*&lt;/td&gt;&lt;td&gt;/bookings&lt;/td&gt;&lt;td&gt;/hotels/bookings&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels/&#42;&#42;&lt;/td&gt;&lt;td&gt;/bookings&lt;/td&gt;&lt;td&gt;/hotels/&#42;&#42;/bookings&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;td&gt;{hotel}&lt;/td&gt;&lt;td&gt;/hotels/{hotel}&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels/*&lt;/td&gt;&lt;td&gt;{hotel}&lt;/td&gt;&lt;td&gt;/hotels/{hotel}&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/hotels/&#42;&#42;&lt;/td&gt;&lt;td&gt;{hotel}&lt;/td&gt;&lt;td&gt;/hotels/&#42;&#42;/{hotel}&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/*.html&lt;/td&gt;&lt;td&gt;/hotels.html&lt;/td&gt;&lt;td&gt;/hotels.html&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/*.html&lt;/td&gt;&lt;td&gt;/hotels&lt;/td&gt;&lt;td&gt;/hotels.html&lt;/td&gt;&lt;/tr&gt;
     * &lt;tr&gt;&lt;td&gt;/*.html&lt;/td&gt;&lt;td&gt;/*.txt&lt;/td&gt;&lt;td&gt;{@code IllegalArgumentException}&lt;/td&gt;&lt;/tr&gt;
     * &lt;/table&gt;
     * @param pattern1 the first pattern
     * @param pattern2 the second pattern
     * @return the combination of the two patterns
     * @throws IllegalArgumentException if the two patterns cannot be combined
     */
    public String combine(String pattern1, String pattern2) {
        if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2)) {
            return "";
        }
        if (!StringUtils.hasText(pattern1)) {
            return pattern2;
        }
        if (!StringUtils.hasText(pattern2)) {
            return pattern1;
        }

        boolean pattern1ContainsUriVar = (pattern1.indexOf('{') != -1);
        if (!pattern1.equals(pattern2) && !pattern1ContainsUriVar && match(pattern1, pattern2)) {
            // /* + /hotel -> /hotel ; "/*.*" + "/*.html" -> /*.html
            // However /user + /user -> /usr/user ; /{foo} + /bar -> /{foo}/bar
            return pattern2;
        }

        // /hotels/* + /booking -> /hotels/booking
        // /hotels/* + booking -> /hotels/booking
        if (pattern1.endsWith(this.pathSeparatorPatternCache.getEndsOnWildCard())) {
            return concat(pattern1.substring(0, pattern1.length() - 2), pattern2);
        }

        // /hotels/** + /booking -> /hotels/**/booking
        // /hotels/** + booking -> /hotels/**/booking
        if (pattern1.endsWith(this.pathSeparatorPatternCache.getEndsOnDoubleWildCard())) {
            return concat(pattern1, pattern2);
        }

        int starDotPos1 = pattern1.indexOf("*.");
        if (pattern1ContainsUriVar || starDotPos1 == -1 || ".".equals(this.pathSeparator)) {
            // simply concatenate the two patterns
            return concat(pattern1, pattern2);
        }

        String ext1 = pattern1.substring(starDotPos1 + 1);
        int dotPos2 = pattern2.indexOf('.');
        String file2 = (dotPos2 == -1 ? pattern2 : pattern2.substring(0, dotPos2));
        String ext2 = (dotPos2 == -1 ? "" : pattern2.substring(dotPos2));
        boolean ext1All = (".*".equals(ext1) || ext1.isEmpty());
        boolean ext2All = (".*".equals(ext2) || ext2.isEmpty());
        if (!ext1All && !ext2All) {
            throw new IllegalArgumentException("Cannot combine patterns: " + pattern1 + " vs " + pattern2);
        }
        String ext = (ext1All ? ext2 : ext1);
        return file2 + ext;
    }

    private String concat(String path1, String path2) {
        boolean path1EndsWithSeparator = path1.endsWith(this.pathSeparator);
        boolean path2StartsWithSeparator = path2.startsWith(this.pathSeparator);

        if (path1EndsWithSeparator && path2StartsWithSeparator) {
            return path1 + path2.substring(1);
        } else if (path1EndsWithSeparator || path2StartsWithSeparator) {
            return path1 + path2;
        } else {
            return path1 + this.pathSeparator + path2;
        }
    }

    /**
     * Given a full path, returns a {@link Comparator} suitable for sorting patterns in order of
     * explicitness.
     * &lt;p&gt;This{@code Comparator} will {@linkplain java.util.List#sort(Comparator) sort}
     * a list so that more specific patterns (without uri templates or wild cards) come before
     * generic patterns. So given a list with the following patterns:
     * &lt;ol&gt;
     * &lt;li&gt;{@code /hotels/new}&lt;/li&gt;
     * &lt;li&gt;{@code /hotels/{hotel}}&lt;/li&gt; &lt;li&gt;{@code /hotels/*}&lt;/li&gt;
     * &lt;/ol&gt;
     * the returned comparator will sort this list so that the order will be as indicated.
     * &lt;p&gt;The full path given as parameter is used to test for exact matches. So when the given path
     * is {@code /hotels/2}, the pattern {@code /hotels/2} will be sorted before {@code /hotels/1}.
     * @param path the full path to use for comparison
     * @return a comparator capable of sorting patterns in order of explicitness
     */
    public Comparator<String> getPatternComparator(String path) {
        return new AntPatternComparator(path);
    }


    /**
     * Tests whether or not a string matches against a pattern via a {@link Pattern}.
     * &lt;p&gt;The pattern may contain special characters: '*' means zero or more characters; '?' means one and
     * only one character; '{' and '}' indicate a URI template pattern. For example &lt;tt&gt;/users/{user}&lt;/tt&gt;.
     */
    protected static class AntPathStringMatcher {

        private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");

        private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";

        private final Pattern pattern;

        private final List<String> variableNames = new LinkedList<>();

        public AntPathStringMatcher(String pattern) {
            this(pattern, true);
        }

        public AntPathStringMatcher(String pattern, boolean caseSensitive) {
            StringBuilder patternBuilder = new StringBuilder();
            Matcher matcher = GLOB_PATTERN.matcher(pattern);
            int end = 0;
            while (matcher.find()) {
                patternBuilder.append(quote(pattern, end, matcher.start()));
                String match = matcher.group();
                if ("?".equals(match)) {
                    patternBuilder.append('.');
                } else if ("*".equals(match)) {
                    patternBuilder.append(".*");
                } else if (match.startsWith("{") && match.endsWith("}")) {
                    int colonIdx = match.indexOf(':');
                    if (colonIdx == -1) {
                        patternBuilder.append(DEFAULT_VARIABLE_PATTERN);
                        this.variableNames.add(matcher.group(1));
                    } else {
                        String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                        patternBuilder.append('(');
                        patternBuilder.append(variablePattern);
                        patternBuilder.append(')');
                        String variableName = match.substring(1, colonIdx);
                        this.variableNames.add(variableName);
                    }
                }
                end = matcher.end();
            }
            patternBuilder.append(quote(pattern, end, pattern.length()));
            this.pattern = (caseSensitive ? Pattern.compile(patternBuilder.toString()) :
                    Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE));
        }

        private String quote(String s, int start, int end) {
            if (start == end) {
                return "";
            }
            return Pattern.quote(s.substring(start, end));
        }

        /**
         * Main entry point.
         * @param str target
         * @param uriTemplateVariables patterns
         * @return {@code true} if the string matches against the pattern, or {@code false} otherwise.
         */
        public boolean matchStrings(String str, Map<String, String> uriTemplateVariables) {
            Matcher matcher = this.pattern.matcher(str);
            if (matcher.matches()) {
                if (uriTemplateVariables != null) {
                    // SPR-8455
                    if (this.variableNames.size() != matcher.groupCount()) {
                        throw new IllegalArgumentException("The number of capturing groups in the pattern segment " +
                                this.pattern + " does not match the number of URI template variables it defines, " +
                                "which can occur if capturing groups are used in a URI template regex. " +
                                "Use non-capturing groups instead.");
                    }
                    for (int i = 1; i <= matcher.groupCount(); i++) {
                        String name = this.variableNames.get(i - 1);
                        String value = matcher.group(i);
                        uriTemplateVariables.put(name, value);
                    }
                }
                return true;
            } else {
                return false;
            }
        }
    }


    /**
     * The default {@link Comparator} implementation returned by
     * {@link #getPatternComparator(String)}.
     * &lt;p&gt;In order, the most "generic" pattern is determined by the following:
     * &lt;ul&gt;
     * &lt;li&gt;if it's null or a capture all pattern (i.e. it is equal to "/**")&lt;/li&gt;
     * &lt;li&gt;if the other pattern is an actual match&lt;/li&gt;
     * &lt;li&gt;if it's a catch-all pattern (i.e. it ends with "**"&lt;/li&gt;
     * &lt;li&gt;if it's got more "*" than the other pattern&lt;/li&gt;
     * &lt;li&gt;if it's got more "{foo}" than the other pattern&lt;/li&gt;
     * &lt;li&gt;if it's shorter than the other pattern&lt;/li&gt;
     * &lt;/ul&gt;
     */
    protected static class AntPatternComparator implements Comparator<String> {

        private final String path;

        public AntPatternComparator(String path) {
            this.path = path;
        }

        /**
         * Compare two patterns to determine which should match first, i.e. which
         * is the most specific regarding the current path.
         * @return a negative integer, zero, or a positive integer as pattern1 is
         * more specific, equally specific, or less specific than pattern2.
         */
        @Override
        public int compare(String pattern1, String pattern2) {
            PatternInfo info1 = new PatternInfo(pattern1);
            PatternInfo info2 = new PatternInfo(pattern2);

            if (info1.isLeastSpecific() && info2.isLeastSpecific()) {
                return 0;
            } else if (info1.isLeastSpecific()) {
                return 1;
            } else if (info2.isLeastSpecific()) {
                return -1;
            }

            boolean pattern1EqualsPath = pattern1.equals(this.path);
            boolean pattern2EqualsPath = pattern2.equals(this.path);
            if (pattern1EqualsPath && pattern2EqualsPath) {
                return 0;
            } else if (pattern1EqualsPath) {
                return -1;
            } else if (pattern2EqualsPath) {
                return 1;
            }

            if (info1.isPrefixPattern() && info2.getDoubleWildcards() == 0) {
                return 1;
            } else if (info2.isPrefixPattern() && info1.getDoubleWildcards() == 0) {
                return -1;
            }

            if (info1.getTotalCount() != info2.getTotalCount()) {
                return info1.getTotalCount() - info2.getTotalCount();
            }

            if (info1.getLength() != info2.getLength()) {
                return info2.getLength() - info1.getLength();
            }

            if (info1.getSingleWildcards() < info2.getSingleWildcards()) {
                return -1;
            } else if (info2.getSingleWildcards() < info1.getSingleWildcards()) {
                return 1;
            }

            if (info1.getUriVars() < info2.getUriVars()) {
                return -1;
            } else if (info2.getUriVars() < info1.getUriVars()) {
                return 1;
            }

            return 0;
        }


        /**
         * Value class that holds information about the pattern, e.g. number of
         * occurrences of "*", "**", and "{" pattern elements.
         */
        private static class PatternInfo {

            private final String pattern;

            private int uriVars;

            private int singleWildcards;

            private int doubleWildcards;

            private boolean catchAllPattern;

            private boolean prefixPattern;

            private Integer length;

            public PatternInfo(String pattern) {
                this.pattern = pattern;
                if (this.pattern != null) {
                    initCounters();
                    this.catchAllPattern = "/**".equals(this.pattern);
                    this.prefixPattern = !this.catchAllPattern && this.pattern.endsWith("/**");
                }
                if (this.uriVars == 0) {
                    this.length = (this.pattern != null ? this.pattern.length() : 0);
                }
            }

            protected void initCounters() {
                int pos = 0;
                if (this.pattern != null) {
                    while (pos < this.pattern.length()) {
                        if (this.pattern.charAt(pos) == '{') {
                            this.uriVars++;
                            pos++;
                        } else if (this.pattern.charAt(pos) == '*') {
                            if (pos + 1 < this.pattern.length() && this.pattern.charAt(pos + 1) == '*') {
                                this.doubleWildcards++;
                                pos += 2;
                            } else if (pos > 0 && !".*".equals(this.pattern.substring(pos - 1))) {
                                this.singleWildcards++;
                                pos++;
                            } else {
                                pos++;
                            }
                        } else {
                            pos++;
                        }
                    }
                }
            }

            public int getUriVars() {
                return this.uriVars;
            }

            public int getSingleWildcards() {
                return this.singleWildcards;
            }

            public int getDoubleWildcards() {
                return this.doubleWildcards;
            }

            public boolean isLeastSpecific() {
                return (this.pattern == null || this.catchAllPattern);
            }

            public boolean isPrefixPattern() {
                return this.prefixPattern;
            }

            public int getTotalCount() {
                return this.uriVars + this.singleWildcards + (2 * this.doubleWildcards);
            }

            /**
             * Returns the length of the given pattern, where template variables are considered to be 1 long.
             */
            public int getLength() {
                if (this.length == null) {
                    this.length = (this.pattern != null ?
                            VARIABLE_PATTERN.matcher(this.pattern).replaceAll("#").length() : 0);
                }
                return this.length;
            }
        }
    }


    /**
     * A simple cache for patterns that depend on the configured path separator.
     */
    private static class PathSeparatorPatternCache {

        private final String endsOnWildCard;

        private final String endsOnDoubleWildCard;

        public PathSeparatorPatternCache(String pathSeparator) {
            this.endsOnWildCard = pathSeparator + "*";
            this.endsOnDoubleWildCard = pathSeparator + "**";
        }

        public String getEndsOnWildCard() {
            return this.endsOnWildCard;
        }

        public String getEndsOnDoubleWildCard() {
            return this.endsOnDoubleWildCard;
        }
    }

}