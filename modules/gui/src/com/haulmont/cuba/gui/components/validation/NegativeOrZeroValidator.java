/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberConstraint;

import static com.haulmont.cuba.gui.components.validation.ConstraintHelper.getNumberConstraint;

public class NegativeOrZeroValidator<T extends Number> extends AbstractValidator<T> {

    public NegativeOrZeroValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.negativeOrZero");
    }

    public NegativeOrZeroValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = getNumberConstraint(value);
        if (constraint == null) {
            throw new IllegalArgumentException("NegativeOrZeroValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isNegativeOrZero()) {
            throw new ValidationException(String.format(getErrorMessage(), value));
        }
    }
}
