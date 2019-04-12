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

import com.google.common.base.Strings;
import com.haulmont.chile.core.datatypes.Datatypes;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.gui.GuiDevelopmentException;
import com.haulmont.cuba.gui.components.Buffered;
import com.haulmont.cuba.gui.components.Field;
import com.haulmont.cuba.gui.components.HasDatatype;
import com.haulmont.cuba.gui.components.validation.*;
import com.haulmont.cuba.gui.components.validators.EmailValidator;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.List;

public abstract class AbstractFieldLoader<T extends Field> extends AbstractDatasourceComponentLoader<T> {

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
        Element validators = element.element("validators");
        if (validators != null) {
            Element notEmptyElement = validators.element("notEmpty");
            if (notEmptyElement != null) {
                NotEmptyValidator notEmptyValidator = new NotEmptyValidator();
                loadValidatorMessage(notEmptyValidator, notEmptyElement);
                component.addValidator(notEmptyValidator);
            }

            Element notBlankElement = validators.element("notBlank");
            if (notBlankElement != null) {
                NotBlankValidator notBlankValidator = new NotBlankValidator();
                loadValidatorMessage(notBlankValidator, notBlankElement);
                component.addValidator(notBlankValidator);
            }

            Element regexpElement = validators.element("regexp");
            if (regexpElement != null) {
                String regexp = regexpElement.attributeValue("regexp");
                RegexpValidator regexpValidator = new RegexpValidator(regexp);
                loadValidatorMessage(regexpValidator, regexpElement);
                component.addValidator(regexpValidator);
            }

            Element sizeElement = validators.element("size");
            if (sizeElement != null) {
                SizeValidator sizeValidator = new SizeValidator();

                String min = sizeElement.attributeValue("min");
                if (min != null) {
                    int minValue = Integer.parseInt(min);
                    if (minValue < 0) {
                        throw new GuiDevelopmentException("Min value must be greater or equal to 0",
                                context.getFullFrameId());
                    }
                    sizeValidator.withMin(minValue);
                }

                String max = sizeElement.attributeValue("max");
                if (max != null) {
                    int maxValue = Integer.parseInt(max);
                    if (maxValue < 0) {
                        throw new GuiDevelopmentException("Max value must be greater or equal to 0",
                                context.getFullFrameId());
                    }
                    sizeValidator.withMax(maxValue);
                }

                loadValidatorMessage(sizeValidator, sizeElement);
                component.addValidator(sizeValidator);
            }

            Element mustBeNotNullElement = validators.element("notNull");
            if (mustBeNotNullElement != null) {
                NotNullValidator notNullValidator = new NotNullValidator<>();
                loadValidatorMessage(notNullValidator, mustBeNotNullElement);
                component.addValidator(notNullValidator);
            }

            Element negativeOrZeroElement = validators.element("negativeOrZero");
            if (negativeOrZeroElement != null) {
                NegativeOrZeroValidator negativeOrZeroValidator = new NegativeOrZeroValidator<>();
                loadValidatorMessage(negativeOrZeroValidator, negativeOrZeroElement);
                component.addValidator(negativeOrZeroValidator);
            }

            Element negativeElement = validators.element("negative");
            if (negativeElement != null) {
                NegativeValidator negativeValidator = new NegativeValidator<>();
                loadValidatorMessage(negativeValidator, negativeElement);
                component.addValidator(negativeValidator);
            }

            Element positiveOrZeroElement = validators.element("positiveOrZero");
            if (positiveOrZeroElement != null) {
                PositiveOrZeroValidator positiveOrZeroValidator = new PositiveOrZeroValidator<>();
                loadValidatorMessage(positiveOrZeroValidator, positiveOrZeroElement);
                component.addValidator(positiveOrZeroValidator);
            }

            Element positiveElement = validators.element("positive");
            if (positiveElement != null) {
                PositiveValidator positiveValidator = new PositiveValidator<>();
                loadValidatorMessage(positiveValidator, positiveElement);
                component.addValidator(positiveValidator);
            }

            Element maxElement = validators.element("max");
            if (maxElement != null) {
                MaxValidator maxValidator = new MaxValidator<>();
                loadValidatorMessage(maxValidator, maxElement);

                String max = maxElement.attributeValue("value");
                if (max != null) {
                    int maxValue = Integer.parseInt(max);
                    maxValidator.withMax(maxValue);
                }
                component.addValidator(maxValidator);
            }

            Element minElement = validators.element("min");
            if (minElement != null) {
                MinValidator minValidator = new MinValidator<>();
                loadValidatorMessage(minValidator, minElement);

                String min = minElement.attributeValue("value");
                if (min != null) {
                    int minValue = Integer.parseInt(min);
                    minValidator.withMin(minValue);
                }
                component.addValidator(minValidator);
            }

            Element decimalMinElement = validators.element("decimalMin");
            if (decimalMinElement != null) {
                DecimalMinValidator decimalMinValidator = new DecimalMinValidator<>();
                loadValidatorMessage(decimalMinValidator, decimalMinElement);

                String decimalMin = decimalMinElement.attributeValue("value");
                if (StringUtils.isNotBlank(decimalMin)) {
                    decimalMinValidator.withMin(decimalMin);
                }

                String inclusive = decimalMinElement.attributeValue("inclusive");
                if (StringUtils.isNotBlank(inclusive)) {
                    decimalMinValidator.withInclusive(Boolean.parseBoolean(inclusive));
                }
                component.addValidator(decimalMinValidator);
            }

            Element decimalMaxElement = validators.element("decimalMax");
            if (decimalMaxElement != null) {
                DecimalMaxValidator decimalMaxValidator = new DecimalMaxValidator<>();
                loadValidatorMessage(decimalMaxValidator, decimalMaxElement);

                String decimalMax = decimalMaxElement.attributeValue("value");
                if (StringUtils.isNotBlank(decimalMax)) {
                    decimalMaxValidator.withMax(decimalMax);
                }

                String inclusive = decimalMaxElement.attributeValue("inclusive");
                if (StringUtils.isNotBlank(inclusive)) {
                    decimalMaxValidator.withInclusive(Boolean.parseBoolean(inclusive));
                }
                component.addValidator(decimalMaxValidator);
            }

            Element digitsElement = validators.element("digits");
            if (digitsElement != null) {
                DigitsValidator digitsValidator;

                String integer = digitsElement.attributeValue("integer");
                String fraction = digitsElement.attributeValue("fraction");
                if (StringUtils.isNotBlank(integer) || StringUtils.isNotBlank(fraction)) {
                    digitsValidator = new DigitsValidator<>(Integer.parseInt(integer), Integer.parseInt(fraction));
                } else {
                    throw new GuiDevelopmentException("'integer' and 'fraction' properties are required", context.getFullFrameId());
                }

                loadValidatorMessage(digitsValidator, digitsElement);
                component.addValidator(digitsValidator);
            }

            Element pastElement = validators.element("past");
            if (pastElement != null) {
                PastValidator pastValidator = new PastValidator();
                String includeSeconds = pastElement.attributeValue("includeSeconds");
                if (StringUtils.isNotBlank(includeSeconds)) {
                    pastValidator.withCheckSeconds(Boolean.parseBoolean(includeSeconds));
                }

                loadValidatorMessage(pastValidator, pastElement);
                component.addValidator(pastValidator);
            }

            Element pastOrPresentElement = validators.element("pastOrPresent");
            if (pastOrPresentElement != null) {
                PastOrPresentValidator pastOrPresentValidator = new PastOrPresentValidator();
                String includeSeconds = pastOrPresentElement.attributeValue("includeSeconds");
                if (StringUtils.isNotBlank(includeSeconds)) {
                    pastOrPresentValidator.withCheckSeconds(Boolean.parseBoolean(includeSeconds));
                }

                loadValidatorMessage(pastOrPresentValidator, pastOrPresentElement);
                component.addValidator(pastOrPresentValidator);
            }

            Element futureElement = validators.element("future");
            if (futureElement != null) {
                FutureValidator futureValidator = new FutureValidator();
                String includeSeconds = futureElement.attributeValue("includeSeconds");
                if (StringUtils.isNotBlank(includeSeconds)) {
                    futureValidator.withCheckSeconds(Boolean.parseBoolean(includeSeconds));
                }

                loadValidatorMessage(futureValidator, futureElement);
                component.addValidator(futureValidator);
            }

            Element futureOrPresentElement = validators.element("futureOrPresent");
            if (futureOrPresentElement != null) {
                FutureOrPresentValidator futureOrPresentValidator = new FutureOrPresentValidator();
                String includeSeconds = futureOrPresentElement.attributeValue("includeSeconds");
                if (StringUtils.isNotBlank(includeSeconds)) {
                    futureOrPresentValidator.withCheckSeconds(Boolean.parseBoolean(includeSeconds));
                }

                loadValidatorMessage(futureOrPresentValidator, futureOrPresentElement);
                component.addValidator(futureOrPresentValidator);
            }

            Element emailElement = validators.element("email");
            if (emailElement != null) {
                component.addValidator(new EmailValidator(emailElement, getMessagesPack()));
            }
        }
    }

    protected void loadValidatorMessage(AbstractValidator validator, Element element) {
        String message = element.attributeValue("message");
        if (message != null) {
            validator.setMessage(loadResourceString(message));
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