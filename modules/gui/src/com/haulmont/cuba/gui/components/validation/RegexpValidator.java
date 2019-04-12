/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.google.common.base.Preconditions;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ValidationException;

import java.util.regex.Pattern;

/**
 * Regexp validator checks that String value is matched with specified regular expression.
 * <p>
 * The regular expression follows the Java regular expression conventions.
 * <p>
 * For error message it uses Groovy string and it is possible to use '$value' key for formatted output.
 *
 * @see java.util.regex.Pattern
 */
public class RegexpValidator extends AbstractValidator<String> {

    protected Pattern pattern;

    public RegexpValidator(String regexp) {
        Preconditions.checkNotNull(regexp);

        this.defaultMessage = messages.getMainMessage("validation.constraints.regexp");
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
        Preconditions.checkNotNull(regexp);

        this.message = message;
        this.pattern = Pattern.compile(regexp);
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
