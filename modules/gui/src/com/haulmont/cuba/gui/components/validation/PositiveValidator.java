/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberConstraint;

import static com.haulmont.cuba.gui.components.validation.ConstraintHelper.getNumberConstraint;

/**
 * Positive validator checks that value should be a strictly greater than 0.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' key for formatted output.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer, Double, Float
 */
public class PositiveValidator<T extends Number> extends AbstractValidator<T> {

    public PositiveValidator() {
        this.defaultMessage = messages.getMainMessage("validation.constraints.positive");
    }

    /**
     * Constructor for custom error message. This message can contain '$value' key for formatted output.
     * <p>
     * Example: "Value '$value' should be greater than 0".
     *
     * @param message error message
     */
    public PositiveValidator(String message) {
        this.message = message;
    }

    @Override
    public void accept(T value) {
        // consider null is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = getNumberConstraint(value);
        if (constraint == null) {
            throw new IllegalArgumentException("PositiveValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isPositive()) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value)));
        }
    }
}
