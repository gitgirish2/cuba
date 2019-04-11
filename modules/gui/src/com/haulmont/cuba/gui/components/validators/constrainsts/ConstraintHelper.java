/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts;

import com.haulmont.cuba.gui.components.validators.constrainsts.numbers.*;
import com.haulmont.cuba.gui.components.validators.constrainsts.time.AbstractTimeConstraint.*;
import com.haulmont.cuba.gui.components.validators.constrainsts.time.TimeConstraint;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Date;

public final class ConstraintHelper {

    @Nullable
    public static NumberConstraint getNumberConstraint(Number value) {
        if (value == null) {
            return null;
        }

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

    @Nullable
    public static <T> TimeConstraint getTimeConstraint(T value) {
        if (value == null) {
            return null;
        }

        Class clazz = value.getClass();
        if (clazz.equals(Date.class)) {
            return new DateConstraint((Date) value);
        } else if (clazz.equals(LocalDate.class)) {
            return new LocalDateConstraint((LocalDate) value);
        } else if (clazz.equals(LocalDateTime.class)) {
            return new LocalDateTimeConstraint((LocalDateTime) value);
        } else if (clazz.equals(LocalTime.class)) {
            return new LocalTimeConstraint((LocalTime) value);
        } else if (clazz.equals(OffsetTime.class)) {
            return new OffsetTimeConstraint((OffsetTime) value);
        } else if (clazz.equals(OffsetDateTime.class)) {
            return new OffsetDateTimeConstraint((OffsetDateTime) value);
        }
        return null;
    }
}
