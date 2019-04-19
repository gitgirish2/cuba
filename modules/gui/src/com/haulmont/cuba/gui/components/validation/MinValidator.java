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
 * Min validator checks that value must be greater or equal to the specified minimum.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' and '$min' keys for formatted output.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *    &lt;bean id="MinValidator" class="com.haulmont.cuba.gui.components.validation.MinValidator" scope="prototype"/&gt;
 *    </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer
 */
@Component(MinValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class MinValidator<T extends Number> extends AbstractValidator<T> {

    public static final String NAME = "cuba_MinValidator";

    protected long min;

    protected String defaultMessage = messages.getMainMessage("validation.constraints.min");

    /**
     * Constructor with default error message.
     *
     * @param min min value
     */
    public MinValidator(long min) {
        this.min = min;
    }

    /**
     * Constructor with custom error message. This message can contain '$value' and '$min' keys for formatted output.
     * <p>
     * Example: "Value '$value' should be greater than or equal to '$min'".
     *
     * @param min     min value
     * @param message error message
     */
    public MinValidator(long min, String message) {
        this.min = min;
        this.message = message;
    }

    /**
     * @param element     'min' element
     * @param messagePack message pack
     */
    public MinValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);

        String min = element.attributeValue("value");
        Preconditions.checkNotNullArgument(min);
        this.min = Long.parseLong(min);
    }

    /**
     * Creates validator with default error message.
     *
     * @param min min value
     * @param <T> BigDecimal, BigInteger, Long, Integer
     * @return validator
     */
    public static <T extends Number> MinValidator<T> create(long min) {
        return AppBeans.getPrototype(NAME, min);
    }

    /**
     * Creates validator with custom error message. This message can contain '$value' and '$min' keys for formatted
     * output.
     * <p>
     * Example: "Value '$value' should be greater than or equal to '$min'".
     *
     * @param min     min value
     * @param message error message
     * @param <T>     BigDecimal, BigInteger, Long, Integer
     * @return validator
     */
    public static <T extends Number> MinValidator<T> create(long min, String message) {
        return AppBeans.getPrototype(NAME, min, message);
    }

    /**
     * @param element     'min' element
     * @param messagePack message pack
     * @param <T>         BigDecimal, BigInteger, Long, Integer
     * @return validator
     */
    public static <T extends Number> MinValidator<T> create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    /**
     * Sets min value.
     *
     * @param min min value
     * @return current instance
     */
    public MinValidator<T> withMin(long min) {
        this.min = min;
        return this;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /**
     * @return min value
     */
    public long getMin() {
        return min;
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
            throw new IllegalArgumentException("MinValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isMin(min)) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value, "min", min)));
        }
    }
}
