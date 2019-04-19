/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.components.validation.numbers.NumberValidator;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.haulmont.cuba.gui.components.validation.ValidatorHelper.getNumberConstraint;

/**
 * PositiveOrZero validator checks that value should be a greater than or equal 0.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' key for formatted output.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *     &lt;bean id="cuba_PositiveOrZeroValidator" class="com.haulmont.cuba.gui.components.validation.PositiveOrZeroValidator" scope="prototype"/&gt;
 *     </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @param <T> BigDecimal, BigInteger, Long, Integer, Double, Float
 */
@Component(PositiveOrZeroValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PositiveOrZeroValidator<T extends Number> extends AbstractValidator<T> {

    public static final String NAME = "cuba_PositiveOrZeroValidator";

    protected String defaultMessage = messages.getMainMessage("validation.constraints.positiveOrZero");

    public PositiveOrZeroValidator() {
    }

    /**
     * Constructor for custom error message. This message can contain '$value' key for formatted output.
     * <p>
     * Example: "Value '$value' should be greater than or equal to 0".
     *
     * @param message error message
     */
    public PositiveOrZeroValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     positiveOrZero element
     * @param messagePack message pack
     */
    public PositiveOrZeroValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);
    }

    /**
     * Creates validator with default message.
     *
     * @param <T> BigDecimal, BigInteger, Long, Integer, Double, Float
     * @return validator
     */
    public static <T extends Number> PositiveOrZeroValidator<T> create() {
        return AppBeans.getPrototype(NAME);
    }

    /**
     * Creates validator with custom error message. This message can contain '$value' key for formatted output.
     * <p>
     * Example: "Value '$value' should be greater than or equal to 0".
     *
     * @param message error message
     * @param <T>     BigDecimal, BigInteger, Long, Integer, Double, Float
     * @return validator
     */
    public static <T extends Number> PositiveOrZeroValidator<T> create(String message) {
        return AppBeans.getPrototype(NAME, message);
    }

    /**
     * @param element     positiveOrZero element
     * @param messagePack message pack
     * @param <T>         BigDecimal, BigInteger, Long, Integer, Double, Float
     * @return validator
     */
    public static <T extends Number> PositiveOrZeroValidator<T> create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public void accept(T value) throws ValidationException {
        // consider null is valid
        if (value == null) {
            return;
        }

        NumberValidator constraint = getNumberConstraint(value);
        if (constraint == null) {
            throw new IllegalArgumentException("PositiveOrZeroValidator doesn't support following type: '" + value.getClass() + "'");
        }

        if (!constraint.isPositiveOrZero()) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value)));
        }
    }
}
