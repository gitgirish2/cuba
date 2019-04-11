/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

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

public class DigitsValidator<T> extends AbstractValidator<T> {

    protected UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);

    protected int integer;
    protected int fraction;

    public DigitsValidator(int integer, int fraction) {
        this.errorMessage = messages.getMainMessage("validation.constraints.digits");
        this.integer = integer;
        this.fraction = fraction;
    }

    public DigitsValidator(int integer, int fraction, String errorMessage) {
        this.errorMessage = errorMessage;
        this.integer = integer;
        this.fraction = fraction;
    }

    public DigitsValidator<T> withIntger(int integer) {
        this.integer = integer;
        return this;
    }

    public DigitsValidator<T> withFraction(int fraction) {
        this.fraction = fraction;
        return this;
    }

    public int getIntger() {
        return integer;
    }

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
                    throw new ValidationException(messages.formatMainMessage("validation.constraints.digits", value));
                }
                constraint = getNumberConstraint(bigDecimal);
            } catch (ParseException e) {
                throw new ValidationException(e.getLocalizedMessage());
            }
        }

        if (constraint == null || value instanceof Double) {
            throw new IllegalArgumentException("DigitsValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDigits(integer, fraction)) {
            throw new ValidationException(String.format(getErrorMessage(), value, integer, fraction));
        }
    }
}
