/*
 * Copyright (c) 2009 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.
 */
package com.haulmont.cuba.gui.components;

import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.security.entity.FilterEntity;

/**
 * @author krivopustov
 * @version $Id$
 */
public interface Filter
        extends Component.Container, Component.BelongToFrame,
        Component.HasXmlDescriptor, Component.HasSettings {

    String NAME = "filter";

    CollectionDatasource getDatasource();
    void setDatasource(CollectionDatasource datasource);

    void setFilterEntity(FilterEntity filterEntity);

    boolean apply(boolean isNewWindow);

    /**
     * Low-level method, don't invoke from application code
     */
    void loadFiltersAndApplyDefault();

    /**
     * Notice: Always is false for HierarchicalDatasource
     */
    void setUseMaxResults(boolean useMaxResults);
    boolean getUseMaxResults();

    void setApplyTo(Component component);
    Component getApplyTo();
    
    void setManualApplyRequired(Boolean manualApplyRequired);
    Boolean getManualApplyRequired();

    void setEditable(boolean editable);
    boolean isEditable();

    void setRequired(boolean required);
    boolean isRequired();

    void setFolderActionsEnabled(boolean enabled);
    boolean isFolderActionsEnabled();
}