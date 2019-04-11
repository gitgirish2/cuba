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

import com.haulmont.bali.events.Subscription;
import com.haulmont.cuba.gui.components.LayoutClickNotifier;
import com.haulmont.cuba.gui.components.data.ValueSource;

import javax.validation.constraints.Min;

import static com.haulmont.cuba.gui.meta.PropertyType.*;

@SuppressWarnings("unused")
public class SimpleMetaBlueprint {

    private interface BComponent {
        @StudioProperty(type = CSS_CLASSNAME_LIST)
        String setStylename(String stylename);

        @StudioProperty(type = SIZE, category = "Size")
        String setWidth();

        @StudioProperty(type = SIZE, category = "Size")
        String setHeight();
    }

    @StudioComponent(caption = "TextField", category = "Input")
    @PropertiesRule(mode = PropertiesMode.ONE_OF, properties = {"container", "datasource"})
    @PropertiesRule(mode = PropertiesMode.ALL, properties = {"container", "property"})
    private interface BTextField {

        @StudioProperties(properties = {
                @StudioProperty(id = "container", caption = "Container", type = CONTAINER_REF),
                @StudioProperty(id = "property", caption = "Property", type = PROPERTY_PATH_REF)
        })
        void setValueSource(ValueSource valueSource);
    }

    @StudioComponent
    private interface BTextArea {
        @StudioProperty(caption = "Rows Count")
        @Min(1)
        void setRows(int rows);
    }

    private interface BLayout {

        @StudioEvent
        Subscription addLayoutClickListener(LayoutClickNotifier.LayoutClickEvent event);
    }

    @StudioComponent(caption = "GridLayout", tag = "grid", category = "Layout")
    private interface BGridLayout extends BLayout {

    }
}