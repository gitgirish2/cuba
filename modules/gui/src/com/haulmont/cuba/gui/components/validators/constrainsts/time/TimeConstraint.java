/*
 * Copyright (c) 2008-2019 Haulmont. All rights reserved.
 * Use is subject to license terms, see http://www.cuba-platform.com/commercial-software-license for details.
 */

package com.haulmont.cuba.gui.components.validators.constrainsts.time;

public interface TimeConstraint {

    boolean isPast();

    boolean isPastOrPresent();

    boolean isFuture();

    boolean isFutureOrPresent();
}
