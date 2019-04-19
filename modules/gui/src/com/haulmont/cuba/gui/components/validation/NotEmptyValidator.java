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

import java.util.Collection;

/**
 * NotEmpty validator checks that value is not null and not empty.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' keys for formatted output.
 * <p>
 * Note, that size validator for Collection doesn't use key 'value' for output error message.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *     &lt;bean id="cuba_NotEmptyValidator" class="com.haulmont.cuba.gui.components.validation.NotEmptyValidator" scope="prototype"/&gt;
 *     </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @param <T> Collection or String
 */
@Component(NotEmptyValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NotEmptyValidator<T> extends AbstractValidator<T> {

    public static final String NAME = "cuba_NotEmptyValidator";

    protected String defaultMessage = messages.getMainMessage("validation.constraints.notEmpty");

    public NotEmptyValidator() {
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public NotEmptyValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     notEmpty element
     * @param messagePack message pack
     */
    public NotEmptyValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);
    }

    /**
     * Creates validator with default message.
     *
     * @return validator
     */
    public static NotEmptyValidator create() {
        return AppBeans.getPrototype(NAME);
    }

    /**
     * Creates validator with custom error message.
     *
     * @param message error message
     * @return validator
     */
    public static NotEmptyValidator create(String message) {
        return AppBeans.getPrototype(NAME, message);
    }

    /**
     * @param element     notEmpty element
     * @param messagePack message pack
     * @return validator
     */
    public static NotEmptyValidator create(Element element, String messagePack) {
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

        Class clazz = value.getClass();
        if (Collection.class.isAssignableFrom(clazz)) {
            if (((Collection) value).isEmpty()) {
                throw new ValidationException(getErrorMessage());
            }
        } else if (clazz.equals(String.class) && ((String) value).isEmpty()) {
            throw new ValidationException(getErrorMessage());
        }
    }
}
