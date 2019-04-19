/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.ValidationException;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Size validator is applicable for Collections and String values. It checks that value is in a specific range.
 * <p>
 * For error message it uses Groovy string and it is possible to use following keys for formatted output: 'value', 'min' and 'max'.
 * <p>
 * Note, that size validator for Collection doesn't use key 'value' for output error message.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *     &lt;bean id="cuba_SizeValidator" class="com.haulmont.cuba.gui.components.validation.SizeValidator" scope="prototype"/&gt;
 *     </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @param <T> Collection or String
 */
@Component(SizeValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SizeValidator<T> extends AbstractValidator<T> {

    public static final String NAME = "cuba_SizeValidator";

    protected int min;
    protected int max = Integer.MAX_VALUE;

    protected String defaultMessage = messages.getMainMessage("validation.constraints.sizeRange");

    public SizeValidator() {
    }

    /**
     * Constructor for custom error message. This message can contain following keys formatted output:
     * '$value', '$min', and '$max'.
     * <p>
     * Example: "The '$value' length must be between $min and $max".
     * <p>
     * Note, that message for Collection doesn't use '$value' key for output error message.
     *
     * @param message error message
     */
    public SizeValidator(String message) {
        this.message = message;
    }

    /**
     * @param element     size element
     * @param messagePack message pack
     */
    public SizeValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);

        String min = element.attributeValue("min");
        if (min != null) {
            this.min = Integer.parseInt(min);
        }
        String max = element.attributeValue("max");
        if (max != null) {
            this.max = Integer.parseInt(max);
        }
    }

    /**
     * Creates validator with default message.
     *
     * @param <T> Collection or String
     * @return validator
     */
    public static <T> SizeValidator<T> create() {
        return AppBeans.getPrototype(NAME);
    }

    /**
     * Creates validator with custom error message. This message can contain following keys formatted output:
     * '$value', '$min', and '$max'.
     * <p>
     * Example: "The '$value' length must be between $min and $max".
     * <p>
     * Note, that message for Collection doesn't use '$value' key for output error message.
     *
     * @param message error message
     * @param <T>     Collection or String
     * @return validator
     */
    public static <T> SizeValidator<T> create(String message) {
        return AppBeans.getPrototype(NAME, message);
    }

    /**
     * @param element     size element
     * @param messagePack message pack
     * @param <T>         Collection or String
     * @return validator
     */
    public static <T> SizeValidator<T> create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    /**
     * Sets min value of the range. Min value cannot be less than 0. Default value is 0.
     * <p>
     * Note, min value is included in range. Examples:
     * <pre>{@code
     *  value = 0, min = 0 - is valid
     *  value = 1, min = 2 - is not valid
     * }
     * </pre>
     *
     * @param min min value
     * @return current instance
     */
    public SizeValidator<T> withMin(int min) {
        checkPositiveValue(min, "Min value cannot be less then 0");

        this.min = min;
        return this;
    }

    public int getMin() {
        return min;
    }

    /**
     * Sets max value of the range. Max value cannot be less than 0.  Default value is {@link Integer#MAX_VALUE}.
     * <p>
     * Note, max value is included in range. Examples:
     * <pre>{@code
     *  value = 5, max = 5 - is valid
     *  value = 6, max = 5 - is not valid
     * }
     * </pre>
     *
     * @param max max value
     * @return current instance
     */
    public SizeValidator<T> withMax(int max) {
        checkPositiveValue(min, "Max value cannot be less then 0");

        this.max = max;
        return this;
    }

    /**
     * @return max value of the range
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets min and max range values. Min and max values cannot be less than 0.
     *
     * @param min min value
     * @param max max value
     * @return current instance
     */
    public SizeValidator<T> withSize(int min, int max) {
        checkPositiveValue(min, "Min value cannot be less then 0");
        checkPositiveValue(max, "Max value cannot be less then 0");

        this.min = min;
        this.max = max;
        return this;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
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
                this.defaultMessage = messages.getMainMessage("validation.constraints.collectionSizeRange");
                throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("min", min, "max", max)));
            }
        } else if (clazz.equals(String.class)) {
            int length = ((String) value).length();
            if (min > length || length > max) {
                throw new ValidationException(
                        getTemplateErrorMessage(ParamsMap.of(
                                "value", value,
                                "min", min,
                                "max", max)));
            }
        }
    }

    protected void checkPositiveValue(long value, String message) {
        if (value < 0) {
            throw new IllegalArgumentException(message);
        }
    }
}
