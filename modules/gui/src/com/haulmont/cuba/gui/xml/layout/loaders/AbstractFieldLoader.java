/*
 * Copyright (c) 2008-2016 Haulmont.
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
 *
 */
package com.haulmont.cuba.gui.xml.layout.loaders;

import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.gui.components.Buffered;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.HasDatatype;
import com.haulmont.cuba.gui.components.validation.*;
import com.haulmont.cuba.gui.components.validators.EmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class AbstractFieldLoader<T extends Field> extends AbstractDatasourceComponentLoader<T> {

    protected static final Map<String, BiFunction<Element, String, AbstractValidator>> validatorsMap;

    static {
        validatorsMap = new HashMap<>(17);
        validatorsMap.put("notNull", NotNullValidator::new);
        validatorsMap.put("notEmpty", NotEmptyValidator::new);
        validatorsMap.put("notBlank", NotBlankValidator::new);
        validatorsMap.put("regexp", RegexpValidator::new);
        validatorsMap.put("size", SizeValidator::new);
        validatorsMap.put("negativeOrZero", NegativeOrZeroValidator::new);
        validatorsMap.put("negative", NegativeValidator::new);
        validatorsMap.put("positiveOrZero", PositiveOrZeroValidator::new);
        validatorsMap.put("positive", PositiveValidator::new);
        validatorsMap.put("max", MaxValidator::new);
        validatorsMap.put("min", MinValidator::new);
        validatorsMap.put("decimalMin", DecimalMinValidator::new);
        validatorsMap.put("decimalMax", DecimalMaxValidator::new);
        validatorsMap.put("digits", DigitsValidator::new);
        validatorsMap.put("past", PastValidator::new);
        validatorsMap.put("pastOrPresent", PastOrPresentValidator::new);
        validatorsMap.put("future", FutureValidator::new);
        validatorsMap.put("futureOrPresent", FutureOrPresentValidator::new);
    }

    @Override
    public void loadComponent() {
        assignFrame(resultComponent);
        assignXmlDescriptor(resultComponent, element);

        loadContainer(resultComponent, element);
        if (resultComponent.getValueSource() == null) {
            loadDatasource(resultComponent, element);
        }

        loadVisible(resultComponent, element);
        loadEditable(resultComponent, element);
        loadEnable(resultComponent, element);

        loadStyleName(resultComponent, element);

        loadCaption(resultComponent, element);
        loadIcon(resultComponent, element);
        loadDescription(resultComponent, element);
        loadContextHelp(resultComponent, element);

        loadValidators(resultComponent, element);
        loadConstraintValidators(resultComponent, element);

        loadRequired(resultComponent, element);

        loadHeight(resultComponent, element);
        loadWidth(resultComponent, element);
        loadAlign(resultComponent, element);
        loadResponsive(resultComponent, element);
        loadCss(resultComponent, element);
    }

    protected void loadRequired(Field component, Element element) {
        String required = element.attributeValue("required");
        if (StringUtils.isNotEmpty(required)) {
            component.setRequired(Boolean.parseBoolean(required));
        }

        String requiredMessage = element.attributeValue("requiredMessage");
        if (requiredMessage != null) {
            component.setRequiredMessage(loadResourceString(requiredMessage));
        }
    }

    @SuppressWarnings("unchecked")
    protected void loadValidators(Field component, Element element) {
        List<Element> validatorElements = element.elements("validator");

        if (!validatorElements.isEmpty()) {
            for (Element validatorElement : validatorElements) {
                Field.Validator validator = loadValidator(validatorElement);
                if (validator != null) {
                    component.addValidator(validator);
                }
            }

        } else if (component.getDatasource() != null) {
            MetaProperty property = component.getMetaProperty();
            Field.Validator validator = getDefaultValidator(property);
            if (validator != null) {
                component.addValidator(validator);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void loadConstraintValidators(Field component, Element element) {
        Element validatorsHolder = element.element("validators");
        if (validatorsHolder != null) {
            List<Element> validators = validatorsHolder.elements();

            for (Element validator : validators) {
                BiFunction<Element, String, AbstractValidator> validatorLoader = validatorsMap.get(validator.getName());
                if (validatorLoader != null) {
                    component.addValidator(validatorLoader.apply(element, getMessagesPack()));
                } else if (validator.getName().equals("email")) {
                    component.addValidator(new EmailValidator(validator, getMessagesPack()));
                }
            }
        }
    }

    protected void loadBuffered(Buffered component, Element element) {
        String buffered = element.attributeValue("buffered");
        if (StringUtils.isNotEmpty(buffered)) {
            component.setBuffered(Boolean.parseBoolean(buffered));
        }
    }

    protected void loadDatatype(HasDatatype component, Element element) {
        String datatypeAttribute = element.attributeValue("datatype");
        if (StringUtils.isNotEmpty(datatypeAttribute)) {
            //noinspection unchecked
            component.setDatatype(Datatypes.get(datatypeAttribute));
        }
    }
}