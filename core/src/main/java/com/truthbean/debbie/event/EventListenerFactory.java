package com.truthbean.debbie.event;

import java.lang.reflect.Method;
import java.util.EventListener;

public interface EventListenerFactory {

	/**
	 * Specify if this factory supports the specified {@link Method}.
	 * @param method an {@link EventListener} annotated method
	 * @return {@code true} if this factory supports the specified method
	 */
	boolean supportsMethod(Method method);

	/**
	 * Create an {@link DebbieEventListener} for the specified method.
	 * @param beanName the name of the bean
	 * @param type the target type of the instance
	 * @param method the {@link EventListener} annotated method
	 * @return an event listener, suitable to invoke the specified method
	 */
	DebbieEventListener<?> createEventListener(String beanName, Class<?> type, Method method);

}