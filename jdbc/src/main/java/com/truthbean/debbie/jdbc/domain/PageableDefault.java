package com.truthbean.debbie.jdbc.domain;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to set defaults when injecting a {@link Pageable} into a controller
 * method. Instead of configuring {@link #sort()} and {@link #direction()} you can also use {@link SortDefault} or
 * {@link SortDefault.SortDefaults}.
 *
 * @since 1.6
 * @author Oliver Gierke
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageableDefault {

	/**
	 * Alias for {@link #size()}. Prefer to use the {@link #size()} method as it makes the annotation declaration more
	 * expressive and you'll probably want to configure the {@link #page()} anyway.
	 */
	int value() default 10;

	/**
	 * The default-size the injected {@link Pageable} should get if no corresponding
	 * parameter defined in request (default is 10).
	 */
	int size() default 10;

	/**
	 * The default-pagenumber the injected {@link Pageable} should get if no corresponding
	 * parameter defined in request (default is 0).
	 */
	int page() default 0;

	/**
	 * The properties to sort by by default. If unset, no sorting will be applied at all.
	 */
	String[] sort() default {};

	/**
	 * The direction to sort by. Defaults to {@link Sort.Direction#ASC}.
	 */
	Sort.Direction direction() default Sort.Direction.ASC;
}