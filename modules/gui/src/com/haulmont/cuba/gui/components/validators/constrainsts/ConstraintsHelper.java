/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.*;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;

public final class ConstraintsHelper {

    @Nullable
    public static NumberConstraint getNumberConstraint(Number value) {
        Class clazz = value.getClass();
        if (clazz.equals(Integer.class)) {
            return new BigIntegerConstraint(BigInteger.valueOf(value.longValue()));
        } else if (clazz.equals(Long.class) && value.longValue() <= 0) {
            return new LongConstraint(value.longValue());
        } else if (clazz.equals(BigDecimal.class)) {
            return new BigDecimalConstraint((BigDecimal) value);
        } else if (clazz.equals(Double.class)) {
            return new DoubleConstraint(value.doubleValue());
        }
        return null;
    }
}
