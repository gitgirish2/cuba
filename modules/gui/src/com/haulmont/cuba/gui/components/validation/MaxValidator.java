/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberValidator;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.haulmont.cuba.gui.components.validation.ValidatorHelper.getNumberConstraint;

/**
 * Max validator checks that value must be less than or equal to the specified maximum.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' and '$max' keys for formatted output.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *    &lt;bean id="cuba_MaxValidator" class="com.haulmont.cuba.gui.components.validation.MaxValidator" scope="prototype"/&gt;
 *    </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer
 */
@Component(MaxValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MaxValidator<T extends Number> extends AbstractValidator<T> {

    public static final String NAME = "cuba_MaxValidator";

    protected long max;

    protected String defaultMessage = messages.getMainMessage("validation.constraints.max");

    /**
     * Constructor with default error message.
     *
     * @param max max value
     */
    public MaxValidator(long max) {
        this.max = max;
    }

    /**
     * Constructor for custom error message. This message can contain '$value' and '$max' keys for formatted output.
     * <p>
     * Example: "Value '$value' should be less than or equal to '$max'".
     *
     * @param max     max value
     * @param message error message
     */
    public MaxValidator(long max, String message) {
        this.max = max;
        this.message = message;
    }

    /**
     * @param element     'max' element
     * @param messagePack message pack
     */
    public MaxValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);

        String value = element.attributeValue("value");
        Preconditions.checkNotNullArgument(value);
        this.max = Long.parseLong(value);
    }

    /**
     * Creates validator with custom error message. This message can contain '$value' and '$max' keys for formatted
     * output.
     * <p>
     * Example: "Value '$value' should be less than or equal to '$max'".
     *
     * @param max max value
     * @param <T> BigDecimal, BigInteger, Long, Integer
     * @return validator
     */
    public static <T extends Number> MaxValidator<T> create(long max) {
        return AppBeans.getPrototype(NAME, max);
    }

    /**
     * Creates validator with custom error message. This message can contain '$value' and '$max' keys for formatted
     * output.
     * <p>
     * Example: "Value '$value' should be less than or equal to '$max'".
     *
     * @param max     max value
     * @param message error message
     * @param <T>     BigDecimal, BigInteger, Long, Integer
     * @return validator
     */
    public static <T extends Number> MaxValidator<T> create(long max, String message) {
        return AppBeans.getPrototype(NAME, max, message);
    }

    /**
     * @param element     'max' element
     * @param messagePack message pack
     * @param <T>         BigDecimal, BigInteger, Long, Integer
     * @return validator
     */
    public static <T extends Number> MaxValidator<T> create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    /**
     * Sets max value.
     *
     * @param max max value
     * @return current instance
     */
    public MaxValidator<T> withMax(long max) {
        this.max = max;
        return this;
    }

    /**
     * @return max value
     */
    public long getMax() {
        return max;
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

        NumberValidator constraint = getNumberConstraint(value);
        if (constraint == null
                || value instanceof Double
                || value instanceof Float) {
            throw new IllegalArgumentException("MaxValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isMax(max)) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value, "max", max)));
        }
    }
}
