/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.time.TimeConstraint;

public class FutureValidator<T> extends AbstractValidator<T> {

    protected boolean checkSeconds = false;

    public FutureValidator() {
        this.defaultMessage = messages.getMainMessage("validation.constraints.future");
    }

    public FutureValidator(String errorMessage) {
        this.defaultMessage = errorMessage;
    }

    /**
     * Set true if validator should also check seconds and nanos (if supported) in value. Default value is false.
     *
     * @param checkSeconds check seconds
     * @return current instance
     */
    public FutureValidator<T> withCheckSeconds(boolean checkSeconds) {
        this.checkSeconds = checkSeconds;
        return this;
    }

    /**
     * @return true if seconds and nanos are checked
     */
    public boolean isCheckSeconds() {
        return checkSeconds;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        TimeConstraint timeConstraint = ConstraintHelper.getTimeConstraint(value);
        if (timeConstraint == null) {
            throw new IllegalArgumentException("FutureValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!timeConstraint.isFuture()) {
            throw new ValidationException(getMessage());
        }
    }
}
