/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;
import org.dom4j.Element;

/**
 * NotNull validator checks that value is not null.
 *
 * @param <T> value type
 */
public class NotNullValidator<T> extends AbstractValidator<T> {

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
