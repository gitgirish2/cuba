/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validation.time;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.TimeSource;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTimeConstraint<T> implements TimeConstraint {

    protected TimeSource timeSource = AppBeans.get(TimeSource.NAME);
    protected T value;

    protected static Map<Class, TimeConstraint> constraints = new HashMap<>(5);

    @Override
    public boolean isPast() {
        return compareValueWithCurrent() < 0;
    }

    @Override
    public boolean isPastOrPresent() {
        return compareValueWithCurrent() <= 0;
    }

    @Override
    public boolean isFuture() {
        return compareValueWithCurrent() > 0;
    }

    @Override
    public boolean isFutureOrPresent() {
        return compareValueWithCurrent() >= 0;
    }

    public abstract int compareValueWithCurrent();

    public static class DateConstraint extends AbstractTimeConstraint<Date> {

        public DateConstraint(Date value) {
            this.value = value;
        }

        @Override
        public int compareValueWithCurrent() {
            Date currentValue = timeSource.currentTimestamp();
            return value.compareTo(currentValue);
        }

        @Override
        public boolean isFutureOrPresent() {
            Date currentValue = timeSource.currentTimestamp();
            // set 0 sec and millis to currentValue because we don't know what time format is used
            // and in case when we set current HH:mm validator will consider that it is not valid
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentValue);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);

            return value.compareTo(calendar.getTime()) >= 0;
        }
    }

    public static class LocalDateConstraint extends AbstractTimeConstraint<LocalDate> {

        public LocalDateConstraint(LocalDate value) {
            this.value = value;
        }

        @Override
        public int compareValueWithCurrent() {
            LocalDate currentValue = timeSource.now().toLocalDate();
            return value.compareTo(currentValue);
        }
    }

    public static class LocalDateTimeConstraint extends AbstractTimeConstraint<LocalDateTime> {

        public LocalDateTimeConstraint(LocalDateTime value) {
            this.value = value;
        }

        @Override
        public int compareValueWithCurrent() {
            LocalDateTime currentValue = timeSource.now().toLocalDateTime();
            return value.compareTo(currentValue);
        }

        @Override
        public boolean isFutureOrPresent() {
            LocalDateTime currentValue = timeSource.now().toLocalDateTime();
            // set 0 sec and nano to currentValue because we don't know what time format is used
            // and in case when we set current HH:mm validator will consider that it is not valid
            return value.compareTo(currentValue.withSecond(0).withNano(0)) >= 0;
        }
    }

    public static class LocalTimeConstraint extends AbstractTimeConstraint<LocalTime> {

        public LocalTimeConstraint(LocalTime value) {
            this.value = value;
        }

        @Override
        public int compareValueWithCurrent() {
            LocalTime currentValue = timeSource.now().toLocalTime();
            return value.compareTo(currentValue);
        }

        @Override
        public boolean isFutureOrPresent() {
            LocalTime currentValue = timeSource.now().toLocalTime();
            // set 0 sec and nano to currentValue because we don't know what time format is used
            // and in case when we set current HH:mm validator will consider that it is not valid
            return value.compareTo(currentValue.withSecond(0).withNano(0)) >= 0;
        }
    }

    public static class OffsetTimeConstraint extends AbstractTimeConstraint<OffsetTime> {

        public OffsetTimeConstraint(OffsetTime value) {
            this.value = value;
        }

        @Override
        public int compareValueWithCurrent() {
            OffsetTime currentValue = timeSource.now().toOffsetDateTime().toOffsetTime();
            return value.compareTo(currentValue);
        }

        @Override
        public boolean isFutureOrPresent() {
            OffsetTime currentValue = timeSource.now().toOffsetDateTime().toOffsetTime();
            // set 0 sec and nano to currentValue because we don't know what time format is used
            // and in case when we set current HH:mm validator will consider that it is not valid
            return value.compareTo(currentValue.withSecond(0).withNano(0)) >= 0;
        }
    }

    public static class OffsetDateTimeConstraint extends AbstractTimeConstraint<OffsetDateTime> {

        public OffsetDateTimeConstraint(OffsetDateTime value) {
            this.value = value;
        }

        @Override
        public int compareValueWithCurrent() {
            OffsetDateTime currentValue = timeSource.now().toOffsetDateTime();
            return value.compareTo(currentValue);
        }

        @Override
        public boolean isFutureOrPresent() {
            OffsetDateTime currentValue = timeSource.now().toOffsetDateTime();
            // set 0 sec and nano to currentValue because we don't know what time format is used
            // and in case when we set current HH:mm validator will consider that it is not valid
            return value.compareTo(currentValue.withSecond(0).withNano(0)) >= 0;
        }
    }
}
