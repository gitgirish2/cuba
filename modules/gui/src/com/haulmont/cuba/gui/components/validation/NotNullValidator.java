/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.ValidationException;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * NotNull validator checks that value is not null.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *     &lt;bean id="cuba_NotNullValidator" class="com.haulmont.cuba.gui.components.validation.NotNullValidator" scope="prototype"/&gt;
 *     </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @param <T> value type
 */
@Component(NotNullValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NotNullValidator<T> extends AbstractValidator<T> {

    public static final String NAME = "cuba_NotNullValidator";

    protected String defaultMessage = messages.getMainMessage("validation.constraints.notNull");

    public NotNullValidator() {
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public NotNullValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     notNull element
     * @param messagePack message pack
     */
    public NotNullValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);
    }

    /**
     * Creates validator with default message.
     *
     * @param <T> value type
     * @return validator
     */
    public static <T> NotNullValidator<T> create() {
        return AppBeans.getPrototype(NAME);
    }

    /**
     * Creates validator with custom error message.
     *
     * @param message error message
     * @return validator
     */
    public static <T> NotNullValidator<T> create(String message) {
        return AppBeans.getPrototype(NAME, message);
    }

    /**
     * @param element     notNull element
     * @param messagePack message pack
     * @return validator
     */
    public static <T> NotNullValidator<T> create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        if (value == null) {
            throw new ValidationException(getErrorMessage());
        }
    }
}
