/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberValidator;
import org.dom4j.Element;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Locale;

import static com.haulmont.cuba.gui.components.validation.ValidatorHelper.getNumberConstraint;

/**
 * DecimalMax validator checks that value must be less than or equal to the specified maximum.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' and '$max' keys for formatted output.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer and String that represents BigDecimal value with current locale
 */
public class DecimalMaxValidator<T> extends AbstractValidator<T> {

    protected UserSessionSource userSessionSource = AppBeans.get(UserSessionSource.NAME);

    protected BigDecimal max;
    protected boolean inclusive = true;

    protected String defaultMessage = messages.getMainMessage("validation.constraints.decimalMaxInclusive");

    /**
     * Constructor with default error message.
     *
     * @param max representation of the max value according to the {@link BigDecimal} string representation.
     */
    public DecimalMaxValidator(String max) {
        this.max = new BigDecimal(max);
    }

    /**
     * Constructor with custom error message. This message can contain '$value', and '$max' keys for formatted output.
     * <p>
     * Example: "Value '$value' should be less than or equal to '$max'".
     *
     * @param max     representation of the max value according to the {@link BigDecimal} string representation.
     * @param message error message
     */
    public DecimalMaxValidator(String max, String message) {
        this.max = new BigDecimal(max);
        this.message = message;
    }

    /**
     * @param element     decimalMax element
     * @param messagePack message pack
     */
    public DecimalMaxValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);

        String max = element.attributeValue("value");
        Preconditions.checkNotNullArgument(max);
        this.max = new BigDecimal(max);

        String inclusive = element.attributeValue("inclusive");
        if (inclusive != null) {
            this.inclusive = Boolean.parseBoolean(inclusive);
        }

        setDefaultMessageInclusive(this.inclusive);
    }

    /**
     * Sets max value.
     *
     * @param max representation of the max value according to the {@link BigDecimal} string representation.
     * @return current instance
     */
    public DecimalMaxValidator<T> withMax(String max) {
        this.max = new BigDecimal(max);
        return this;
    }

    /**
     * @return representation of the max value according to the {@link BigDecimal} string representation.
     */
    public BigDecimal getMax() {
        return max;
    }

    /**
     * Sets max value and inclusive option.
     *
     * @param max       representation of the max value according to the {@link BigDecimal} string representation.
     * @param inclusive inclusive option
     * @return current instance
     */
    public DecimalMaxValidator<T> withMax(String max, boolean inclusive) {
        this.max = new BigDecimal(max);
        this.inclusive = inclusive;

        setDefaultMessageInclusive(inclusive);

        return this;
    }

    /**
     * Set to true if the value must be less than or equal to the specified maximum. Default value is true.
     *
     * @param inclusive inclusive option
     * @return current instance
     */
    public DecimalMaxValidator<T> withInclusive(boolean inclusive) {
        this.inclusive = inclusive;

        setDefaultMessageInclusive(inclusive);

        return this;
    }

    /**
     * @return true if the value must be less than or equal to the specified maximum
     */
    public boolean isInclusive() {
        return inclusive;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null value is valid
        if (value == null) {
            return;
        }

        NumberValidator constraint = null;

        if (value instanceof Number) {
            constraint = getNumberConstraint((Number) value);
        } else if (value instanceof String) {
            try {
                Datatype datatype = Datatypes.getNN(BigDecimal.class);
                Locale locale = userSessionSource.getUserSession().getLocale();
                BigDecimal bigDecimal = (BigDecimal) datatype.parse((String) value, locale);
                if (bigDecimal == null) {
                    throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value, "max", max)));
                }
                constraint = getNumberConstraint(bigDecimal);
            } catch (ParseException e) {
                throw new ValidationException(e.getLocalizedMessage());
            }
        }

        if (constraint == null
                || value instanceof Double
                || value instanceof Float) {
            throw new IllegalArgumentException("DecimalMaxValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isDecimalMax(max, inclusive)) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value, "max", max)));
        }
    }

    protected void setDefaultMessageInclusive(boolean inclusive) {
        if (inclusive) {
            defaultMessage = messages.getMainMessage("validation.constraints.decimalMaxInclusive");
        } else {
            defaultMessage = messages.getMainMessage("validation.constraints.decimalMax");
        }
    }
}
