/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validators.constrainsts.time.TimeConstraint;

public class PastOrPresentValidator<T> extends AbstractValidator<T> {

    public PastOrPresentValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.pastOrPresent");
    }

    public PastOrPresentValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        TimeConstraint timeConstraint = ConstraintsHelper.getTimeConstraint(value);
        if (timeConstraint == null) {
            throw new IllegalArgumentException("PastOrPresentValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!timeConstraint.isPastOrPresent()) {
            throw new ValidationException(getErrorMessage());
        }
    }
}
