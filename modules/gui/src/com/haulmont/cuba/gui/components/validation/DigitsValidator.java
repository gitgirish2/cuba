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
 * Digits validator checks that value must be a number within accepted range.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value', '$integer' and '$fraction' keys for
 * formatted output.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer and String that represents BigDecimal value with current locale
 */
public class DigitsValidator<T> extends AbstractValidator<T> {

    protected UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);

    protected int integer;
    protected int fraction;

    /**
     * Constructor with default error message.
     *
     * @param integer  maximum number of integral digits
     * @param fraction maximum number of fractional digits
     */
    public DigitsValidator(int integer, int fraction) {
        this.integer = integer;
        this.fraction = fraction;
        this.defaultMessage = messages.getMainMessage("validation.constraints.digits");
    }

    /**
     * Constructor with custom error message. This message can contain '$value', '$integer' and '$fraction' keys for
     * formatted output.
     * <p>
     * Example: "Value '$value' is out of bounds ($integer digits is expected in integer part and $fraction in
     * fractional part)".
     *
     * @param integer  maximum number of integral digits
     * @param fraction maximum number of fractional digits
     * @param message  error message
     */
    public DigitsValidator(int integer, int fraction, String message) {
        this.integer = integer;
        this.fraction = fraction;
        this.message = message;
    }

    /**
     * Sets maximum number of integral digits.
     *
     * @param integer integer value
     * @return current instance
     */
    public DigitsValidator<T> withIntger(int integer) {
        this.integer = integer;
        return this;
    }

    /**
     * Sets maximum number of fractional digits.
     *
     * @param fraction fraction value
     * @return current instance
     */
    public DigitsValidator<T> withFraction(int fraction) {
        this.fraction = fraction;
        return this;
    }

    /**
     * @return maximum number of integral digits
     */
    public int getIntger() {
        return integer;
    }

    /**
     * @return maximum number of fractional digits
     */
    public int getFraction() {
        return fraction;
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
                    throw new ValidationException(getTemplateErrorMessage(
                            ParamsMap.of(
                                    "value", value,
                                    "integer", integer,
                                    "fraction", fraction)));
                }
                constraint = getNumberConstraint(bigDecimal);
            } catch (ParseException e) {
                throw new ValidationException(e.getLocalizedMessage());
            }
        }

        if (constraint == null
                || value instanceof Double
                || value instanceof Float) {
            throw new IllegalArgumentException("DigitsValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDigits(integer, fraction)) {
            throw new ValidationException(getTemplateErrorMessage(
                    ParamsMap.of(
                            "value", value,
                            "integer", integer,
                            "fraction", fraction)));
        }
    }
}
