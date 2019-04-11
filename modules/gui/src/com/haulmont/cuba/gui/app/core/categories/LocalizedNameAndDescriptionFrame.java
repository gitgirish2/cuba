/*
 * Copyright (c) 2008-2017 Haulmont.
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

package com.haulmont.cuba.gui.app.core.categories;

import com.haulmont.cuba.core.entity.LocaleHelper;
import com.haulmont.cuba.core.global.GlobalConfig;
import com.haulmont.cuba.core.global.MessageTools;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

import javax.inject.Inject;
import java.util.*;

@UiController("localizedNameAndDescriptionFrame")
@UiDescriptor("localized-name-description-frame.xml")
public class LocalizedNameAndDescriptionFrame extends AbstractFrame {

    private static final String MESSAGE_PACK = "msg://com.haulmont.cuba.core.entity/";

    @Inject
    protected ScrollBoxLayout localesScrollBox;

    @Inject
    protected UiComponents uiComponents;

    @Inject
    protected GlobalConfig globalConfig;

    @Inject
    protected MessageTools messageTools;


    protected Map<Locale, TextField> namesTextFieldMap = new HashMap<>();
    protected Map<Locale, TextField> descriptionsTextFieldMap = new HashMap<>();

    @Override
    public void init(Map<String, Object> params) {
        Map<String, Locale> map = globalConfig.getAvailableLocales();
        for (Map.Entry<String, Locale> entry : map.entrySet()) {
            localesScrollBox.add(createLabelComponent(entry.getKey() + "|" + entry.getValue().toString()));
            localesScrollBox.add(createLocaleListComponent(entry.getValue(),
                    messageTools.loadString(MESSAGE_PACK + "CategoryAttribute.name"), namesTextFieldMap));
            localesScrollBox.add(createLocaleListComponent(entry.getValue(),
                    messageTools.loadString(MESSAGE_PACK + "CategoryAttribute.description"), descriptionsTextFieldMap));
        }
    }

    protected Component createLocaleListComponent(Locale locale, String key, Map<Locale, TextField> textFieldMap) {
        TextField valueField = uiComponents.create(TextField.TYPE_STRING);
        valueField.setWidth("100%");
        valueField.setCaption(key);

        textFieldMap.put(locale, valueField);

        return valueField;
    }

    protected Component createLabelComponent(String labelText) {
        Label<String> label = uiComponents.create(Label.TYPE_STRING);
        label.setValue(labelText);
        label.setWidth("100%");
        return label;
    }

    public String getNamesValue() {
        return getValue(namesTextFieldMap);
    }

    public String getDescriptionsValue() {
        return getValue(descriptionsTextFieldMap);
    }

    protected String getValue(Map<Locale, TextField> textFieldMap) {
        Properties properties = new Properties();
        for (Map.Entry<Locale, TextField> entry : textFieldMap.entrySet()) {
            if (!entry.getValue().getRawValue().isEmpty()) {
                properties.setProperty(entry.getKey().toString(), entry.getValue().getRawValue());
            }
        }

        return LocaleHelper.convertPropertiesToString(properties);
    }

    public void setNamesValue(String localeBundle) {
        setValue(localeBundle, namesTextFieldMap);
    }

    public void setDescriptionsValue(String localeBundle) {
        setValue(localeBundle, descriptionsTextFieldMap);
    }

    protected void setValue(String localeBundle, Map<Locale, TextField> textFieldMap) {
        if (localeBundle == null || textFieldMap == null) {
            return;
        }

        Map<String, String> localizedNamesMap = LocaleHelper.getLocalizedValuesMap(localeBundle);
        for (Map.Entry<Locale, TextField> textFieldEntry : textFieldMap.entrySet()) {
            String keyLocale = textFieldEntry.getKey().toString();
            textFieldEntry.getValue().setValue(localizedNamesMap.get(keyLocale));
        }
    }
}
