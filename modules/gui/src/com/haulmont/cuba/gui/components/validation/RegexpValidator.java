/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.gui.components.ValidationException;
import org.dom4j.Element;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Regexp validator checks that String value is matched with specified regular expression.
 * <p>
 * The regular expression follows the Java regular expression conventions.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' key for formatted output.
 * <p>
 * In order to provide your own implementation globally, create a subclass and register it in {@code web-spring.xml},
 * for example:
 * <pre>
 *     &lt;bean id="cuba_RegexpValidator" class="com.haulmont.cuba.gui.components.validation.RegexpValidator" scope="prototype"/&gt;
 *     </pre>
 * Use {@code create()} static methods instead of constructors when creating the action programmatically.
 *
 * @see java.util.regex.Pattern
 */
@Component(RegexpValidator.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class RegexpValidator extends AbstractValidator<String> {

    public static final String NAME = "cuba_RegexpValidator";

    protected Pattern pattern;
    protected String defaultMessage = messages.getMainMessage("validation.constraints.regexp");


    public RegexpValidator(String regexp) {
        Preconditions.checkNotNullArgument(regexp);

        this.pattern = Pattern.compile(regexp);
    }

    /**
     * Constructor for regexp value and custom error message. This message can contain '$value' key for formatted output.
     * Example: "Invalid value '$value'".
     *
     * @param regexp  regular expression
     * @param message error message
     */
    public RegexpValidator(String regexp, String message) {
        Preconditions.checkNotNullArgument(regexp);

        this.message = message;
        this.pattern = Pattern.compile(regexp);
    }

    /**
     * @param element     regexp element
     * @param messagePack message pack
     */
    public RegexpValidator(Element element, String messagePack) {
        this.messagePack = messagePack;
        this.message = loadMessage(element);

        String regexp = element.attributeValue("regexp");
        Preconditions.checkNotNullArgument(regexp);
        this.pattern = Pattern.compile(regexp);
    }

    /**
     * Creates validator with default message.
     *
     * @param regexp regular expression
     * @return validator
     */
    public static RegexpValidator create(String regexp) {
        return AppBeans.getPrototype(NAME, regexp);
    }

    /**
     * Creates validator with regexp value and custom error message. This message can contain '$value' key for
     * formatted output.
     * <p>
     * Example: "Invalid value '$value'".
     *
     * @param regexp  regular expression
     * @param message error message
     * @return validator
     */
    public static RegexpValidator create(String regexp, String message) {
        return AppBeans.getPrototype(NAME, regexp, message);
    }

    /**
     * @param element     regexp element
     * @param messagePack message pack
     * @return validator
     */
    public static RegexpValidator create(Element element, String messagePack) {
        return AppBeans.getPrototype(NAME, element, messagePack);
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }

    @Override
    public void accept(String value) throws ValidationException {
        if (value == null) {
            return;
        }

        if (!pattern.matcher((value)).matches()) {
            throw new ValidationException(getTemplateErrorMessage(ParamsMap.of("value", value)));
        }
    }
}
