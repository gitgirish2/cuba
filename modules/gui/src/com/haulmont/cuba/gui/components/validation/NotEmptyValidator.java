/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;

import java.util.Collection;

public class NotEmptyValidator<T> extends AbstractValidator<T> {

    public NotEmptyValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.notEmpty");
    }

    public NotEmptyValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
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
