/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;

/**
 * NotBlank validator checks that value contains at least one non-whitespace character.
 */
public class NotBlankValidator extends AbstractValidator<String> {

    public NotBlankValidator() {
        this.defaultMessage = messages.getMainMessage("validation.constraints.notBlank");
    }

    /**
     * Constructor for custom error message.
     *
     * @param message error message
     */
    public NotBlankValidator(String message) {
        this.message = message;
    }

    @Override
    public void accept(String value) throws ValidationException {
        if (value == null || value.trim().length() == 0) {
            throw new ValidationException(getErrorMessage());
        }
    }
}
