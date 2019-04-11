/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.haulmont.cuba.gui.components.ValidationException;

import java.util.regex.Pattern;

public class RegexpValidator<T extends String> extends AbstractValidator<T> {

    protected Pattern pattern;

    public RegexpValidator(String regexp) {
        Preconditions.checkNotNull(regexp);

        this.errorMessage = messages.getMainMessage("validation.constraints.regexp");
        this.pattern = Pattern.compile(regexp);
    }

    public RegexpValidator(String regexp, String errorMessage) {
        Preconditions.checkNotNull(regexp);

        this.errorMessage = errorMessage;
        this.pattern = Pattern.compile(regexp);
    }

    @Override
    public void accept(T value) throws ValidationException {
        if (value == null || !pattern.matcher((value)).matches()) {
            throw new ValidationException(String.format(getErrorMessage(), Strings.nullToEmpty(value)));
        }
    }
}
