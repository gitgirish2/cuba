/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberConstraint;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import static com.haulmont.cuba.gui.components.validation.ConstraintHelper.getNumberConstraint;

/**
 * DecimalMin validator checks that value must be greater than or equal to the specified minimum.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' and '$min' keys for formatted output.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer and String that represents BigDecimal value with current locale
 */
public class DecimalMinValidator<T> extends AbstractValidator<T> {

    protected UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);

    protected BigDecimal min;
    protected boolean inclusive = true;

    /**
     * Constructor with default error message.
     *
     * @param min representation of the min value according to the {@link BigDecimal} string representation.
     */
    public DecimalMinValidator(String min) {
        this.min = new BigDecimal(min);
        this.defaultMessage = messages.getMainMessage("validation.constraints.decimalMinInclusive");
    }

    /**
     * Constructor with custom error message. This message can contain '$value', and '$min' keys for formatted output.
     * <p>
     * Example: "Value '$value' should be greater than or equal to '$min'".
     *
     * @param min     representation of the min value according to the {@link BigDecimal} string representation.
     * @param message error message
     */
    public DecimalMinValidator(String min, String message) {
        this.min = new BigDecimal(min);
        this.message = message;
    }

    /**
     * Sets min value.
     *
     * @param min representation of the min value according to the {@link BigDecimal} string representation.
     * @return current instance
     */
    public DecimalMinValidator<T> withMin(String min) {
        this.min = new BigDecimal(min);
        return this;
    }

    /**
     * @return representation of the min value according to the {@link BigDecimal} string representation.
     */
    public BigDecimal getMin() {
        return min;
    }

    /**
     * Sets min value and inclusive option.
     *
     * @param min       representation of the min value according to the {@link BigDecimal} string representation
     * @param inclusive inclusive option
     * @return current instance
     */
    public DecimalMinValidator<T> withMin(String min, boolean inclusive) {
        this.min = new BigDecimal(min);
        this.inclusive = inclusive;

        setDefaultMessage(inclusive);

        return this;
    }

    /**
     * Set to true if the value must be greater than or equal to the specified minimum. Default value is true.
     *
     * @param inclusive inclusive option
     * @return current instance
     */
    public DecimalMinValidator<T> withInclusive(boolean inclusive) {
        this.inclusive = inclusive;

        setDefaultMessage(inclusive);

        return this;
    }

    /**
     * @return true if the value must be greater than or equal to the specified minimum
     */
    public boolean isInclusive() {
        return inclusive;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberConstraint constraint = null;

        if (value instanceof Number) {
            constraint = getNumberConstraint((Number) value);
        } else if (value instanceof String) {
            try {
                Datatype datatype = Datatypes.getNN(BigDecimal.class);
                Locale locale = userSessionSource.getUserSession().getLocale();
                BigDecimal bigDecimal = (BigDecimal) datatype.parse((String) value, locale);
                if (bigDecimal == null) {
                    throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value, "min", min)));
                }
                constraint = getNumberConstraint(bigDecimal);
            } catch (ParseException e) {
                throw new ValidationException(e.getLocalizedMessage());
            }
        }

        if (constraint == null
                || value instanceof Double
                || value instanceof Float) {
            throw new IllegalArgumentException("DecimalMinValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDecimalMin(min, inclusive)) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value, "min", min)));
        }
    }

    protected void setDefaultMessage(boolean inclusive) {
        if (inclusive) {
            this.defaultMessage = messages.getMainMessage("validation.constraints.decimalMinInclusive");
        } else {
            this.defaultMessage = messages.getMainMessage("validation.constraints.decimalMin");
        }
    }
}
