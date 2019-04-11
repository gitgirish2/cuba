/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;

public class NotBlankValidator<T extends String> extends AbstractValidator<T> {

    public NotBlankValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.notBlank");
    }

    public NotBlankValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        if (value == null || value.trim().length() == 0) {
            throw new ValidationException(getErrorMessage());
        }
    }
}
