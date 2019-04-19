/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;
import org.dom4j.Element;

/**
 * NotBlank validator checks that value contains at least one non-whitespace character.
 */
public class NotBlankValidator extends AbstractValidator<String> {

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
     * @param element     notEmpty element
     * @param messagePack message pack
     */
    public NotBlankValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);
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
