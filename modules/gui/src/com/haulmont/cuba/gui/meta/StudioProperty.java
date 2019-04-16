/*
 * Copyright (c) 2008-2019 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.gui.meta;

import java.lang.annotation.*;

/**
 * JavaDoc
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface StudioProperty {
    String id() default "";

    String caption() default "";

    String category() default "";

    boolean required() default false;

    String defaultValue() default "";

    PropertyType type() default PropertyType.PRIMITIVE;

    String xmlAttribute() default "";

    String xmlElement() default "";

    /**
     * todo path to custom designer script
     */
    String designer() default "";
}