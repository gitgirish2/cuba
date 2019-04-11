/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.cuba.gui.components.ValidationException;

import java.util.Collection;

public class SizeValidator<T> extends AbstractValidator<T> {

    protected int min;
    protected int max = Integer.MAX_VALUE;

    public SizeValidator() {
        this.errorMessage = messages.getMainMessage("validation.constraints.sizeRange");
    }

    public SizeValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public SizeValidator<T> withMin(int min) {
        checkPositiveValue(min, "Min value cannot be less then 0");

        this.min = min;
        return this;
    }

    public int getMin() {
        return min;
    }

    public SizeValidator<T> withMax(int max) {
        checkPositiveValue(min, "Max value cannot be less then 0");

        this.max = max;
        return this;
    }

    public int getMax() {
        return max;
    }

    public SizeValidator<T> withSize(int min, int max) {
        checkPositiveValue(min, "Min value cannot be less then 0");
        checkPositiveValue(max, "Max value cannot be less then 0");

        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider that null value is in range
        if (value == null) {
            return;
        }

        Class clazz = value.getClass();
        if (Collection.class.isAssignableFrom(clazz)) {
            int size = ((Collection) value).size();
            if (min > size || size > max) {
                throw new ValidationException(String.format(getErrorMessage(), value, min, max));
            }
        } else if (clazz.equals(String.class)) {
            int length = ((String) value).length();
            if (min > length || length > max) {
                throw new ValidationException(String.format(getErrorMessage(), value, min, max));
            }
        }
    }

    protected void checkPositiveValue(long value, String message) {
        if (value < 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
