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
 * NotBlank validator checks that value contains at least one non-whitespace character.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *     &lt;bean id="cuba_NotBlankValidator" class="com.haulmont.cuba.gui.components.validation.NotBlankValidator" scope="prototype"/&gt;
 *     </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 */
@Component(NotBlankValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class NotBlankValidator extends AbstractValidator<String> {

    public static final String NAME = "cuba_NotBlankValidator";

    protected String defaultMessage = messages.getMainMessage("validation.constraints.notBlank");

    public NotBlankValidator() {
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public NotBlankValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     notBlank element
     * @param messagePack message pack
     */
    public NotBlankValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);
    }

    /**
     * Creates validator with default message.
     *
     * @return validator
     */
    public static NotBlankValidator create() {
        return AppBeans.getPrototype(NAME);
    }

    /**
     * Creates validator with custom error message.
     *
     * @param message error message
     * @return validator
     */
    public static NotBlankValidator create(String message) {
        return AppBeans.getPrototype(NAME, message);
    }

    /**
     * @param element     notBlank element
     * @param messagePack message pack
     * @return validator
     */
    public static NotBlankValidator create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public void accept(String value) throws ValidationException {
        if (value == null || value.trim().length() == 0) {
            throw new ValidationException(getErrorMessage());
        }
    }
}
