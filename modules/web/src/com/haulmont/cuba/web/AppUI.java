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

package com.haulmont.cuba.web;

import com.haulmont.cuba.client.ClientUserSession;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.gui.*;
import com.haulmont.cuba.gui.components.RootWindow;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.events.sys.UiEventsMulticaster;
import com.haulmont.cuba.gui.exception.UiExceptionHandler;
import com.haulmont.cuba.gui.screen.Screen;
import com.haulmont.cuba.gui.sys.TestIdManager;
import com.haulmont.cuba.gui.theme.ThemeConstantsRepository;
import com.haulmont.cuba.security.app.UserSessionService;
import com.haulmont.cuba.security.global.LoginException;
import com.haulmont.cuba.security.global.NoUserSessionException;
import com.haulmont.cuba.security.global.UserSession;
import com.haulmont.cuba.web.app.UserSettingsTools;
import com.haulmont.cuba.web.controllers.ControllerUtils;
import com.haulmont.cuba.web.events.UIRefreshEvent;
import com.haulmont.cuba.web.gui.UrlHandlingMode;
import com.haulmont.cuba.web.gui.icons.IconResolver;
import com.haulmont.cuba.gui.UrlRouting;
import com.haulmont.cuba.web.security.events.AppInitializedEvent;
import com.haulmont.cuba.web.security.events.SessionHeartbeatEvent;
import com.haulmont.cuba.web.sys.*;
import com.haulmont.cuba.web.sys.navigation.History;
import com.haulmont.cuba.gui.navigation.NavigationState;
import com.haulmont.cuba.web.sys.navigation.UrlChangeHandler;
import com.haulmont.cuba.web.sys.navigation.WebHistory;
import com.haulmont.cuba.web.widgets.*;
import com.haulmont.cuba.web.widgets.client.ui.CubaUIConstants;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.PaintException;
import com.vaadin.server.PaintTarget;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.WrappedSession;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Single window / page of web application. Root component of Vaadin layout.
 */
@org.springframework.stereotype.Component(AppUI.NAME)
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Push(transport = Transport.WEBSOCKET_XHR)
@PreserveOnRefresh
public class AppUI extends CubaUI implements ErrorHandler, EnhancedUI, UiExceptionHandler.UiContext {

    public static final String NAME = "cuba_AppUI";

    public static final String LAST_REQUEST_ACTION_ATTR = "lastRequestAction";
    public static final String LAST_REQUEST_PARAMS_ATTR = "lastRequestParams";

    private static final Logger log = LoggerFactory.getLogger(AppUI.class);

    protected App app;

    @Inject
    protected Messages messages;
    @Inject
    protected Events events;

    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected WebConfig webConfig;

    @Inject
    protected UserSettingsTools userSettingsTools;
    @Inject
    protected ThemeConstantsRepository themeConstantsRepository;

    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected UserSessionService userSessionService;

    @Inject
    protected UiEventsMulticaster uiEventsMulticaster;

    @Inject
    protected IconResolver iconResolver;
    @Inject
    protected WebJarResourceResolver webJarResourceResolver;

    @Inject
    protected BeanLocator beanLocator;

    protected TestIdManager testIdManager = new TestIdManager();

    protected boolean testMode = false;
    protected boolean performanceTestMode = false;

    protected CubaFileDownloader fileDownloader;

    protected RootWindow topLevelWindow;

    protected Fragments fragments;
    protected Screens screens;
    protected Dialogs dialogs;
    protected Notifications notifications;
    protected WebBrowserTools webBrowserTools;

    protected UrlChangeHandler urlChangeHandler;
    protected UrlRouting urlRouting;
    protected History history;

    protected UserSession currentSession;

    public UserSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(UserSession currentSession) {
        this.currentSession = currentSession;
    }

    public AppUI() {
    }

    /**
     * Dynamically init external JS libraries.
     * You should create JavaScriptExtension class and extend UI object here. <br>
     * <p>
     * Example: <br>
     * <pre><code>
     * JavaScriptExtension:
     *
     * {@literal @}JavaScript("resources/jquery/jquery-1.10.2.min.js")
     * public class JQueryIntegration extends AbstractJavaScriptExtension {
     *
     *     {@literal @}Override
     *     public void extend(AbstractClientConnector target) {
     *         super.extend(target);
     *     }
     *
     *     {@literal @}Override
     *     protected Class&lt;? extends ClientConnector&gt; getSupportedParentType() {
     *         return UI.class;
     *     }
     * }
     *
     * AppUI:
     *
     * protected void initJsLibraries() {
     *     new JQueryIntegration().extend(this);
     * }</code></pre>
     * <p>
     * If you want to include scripts to generated page statically see {@link com.haulmont.cuba.web.sys.CubaBootstrapListener}.
     */
    protected void initJsLibraries() {
    }

    protected void initInternalComponents() {
        fileDownloader = new CubaFileDownloader();
        fileDownloader.extend(this);

        initHistoryBackControl();
    }

    protected void initHistoryBackControl() {
        boolean allowHistoryBack = webConfig.getAllowHandleBrowserHistoryBack();
        UrlHandlingMode urlHandlingMode = webConfig.getUrlHandlingMode();

        if (allowHistoryBack
                && urlHandlingMode != UrlHandlingMode.BACK_ONLY) {
            log.info("Deprecated 'WebConfig#getAllowHandleBrowserHistoryBack' config use is ignored. " +
                    "Please use new 'WebConfig#getUrlHandlingMode' to enable this feature.");
        }

        if (urlHandlingMode == UrlHandlingMode.BACK_ONLY) {
            CubaHistoryControl historyControl = new CubaHistoryControl();
            historyControl.extend(this, this::onHistoryBackPerformed);
        }
    }

    protected App createApplication() {
        return beanLocator.getPrototype(App.NAME);
    }

    /**
     * @return Use {@link #getScreens()} instead.
     */
    @Deprecated
    public WindowManager getWindowManager() {
        return ((WindowManager) screens);
    }

    @Override
    public Screens getScreens() {
        return screens;
    }

    protected void setScreens(Screens screens) {
        this.screens = screens;
    }

    @Override
    public Dialogs getDialogs() {
        return dialogs;
    }

    protected void setDialogs(Dialogs dialogs) {
        this.dialogs = dialogs;
    }

    @Override
    public Notifications getNotifications() {
        return notifications;
    }

    protected void setNotifications(Notifications notifications) {
        this.notifications = notifications;
    }

    @Override
    public WebBrowserTools getWebBrowserTools() {
        return webBrowserTools;
    }

    protected void setWebBrowserTools(WebBrowserTools webBrowserTools) {
        this.webBrowserTools = webBrowserTools;
    }

    @Override
    public Fragments getFragments() {
        return fragments;
    }

    protected void setFragments(Fragments fragments) {
        this.fragments = fragments;
    }

    public UrlRouting getUrlRouting() {
        return urlRouting;
    }

    public void setUrlRouting(UrlRouting urlRouting) {
        this.urlRouting = urlRouting;
    }

    public UrlChangeHandler getUrlChangeHandler() {
        return urlChangeHandler;
    }

    public void setUrlChangeHandler(UrlChangeHandler urlChangeHandler) {
        this.urlChangeHandler = urlChangeHandler;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    @Override
    protected void init(VaadinRequest request) {
        log.trace("Initializing UI {}", this);

        NavigationState requestedState = getUrlRouting().getState();

        try {
            this.testMode = globalConfig.getTestMode();
            this.performanceTestMode = globalConfig.getPerformanceTestMode();
            // init error handlers
            setErrorHandler(this);

            // do not grab focus
            setTabIndex(-1);

            initJsLibraries();

            initInternalComponents();

            if (!App.isBound()) {
                App app = createApplication();
                app.init(request.getLocale());

                this.app = app;

                publishAppInitializedEvent(app);
            } else {
                this.app = App.getInstance();
            }

            Connection connection = app.getConnection();
            if (connection != null && !isUserSessionAlive(connection)) {
                connection.logout();

                Notification.show(
                        messages.getMainMessage("app.sessionExpiredCaption"),
                        messages.getMainMessage("app.sessionExpiredMessage"),
                        Notification.Type.HUMANIZED_MESSAGE);
            }

            if (connection != null) {
                setCurrentSession(connection.getSession());
            }

            setupUI();
        } catch (Exception e) {
            log.error("Unable to init ui", e);

            // unable to connect to middle ware
            showCriticalExceptionMessage(e);
            return;
        }

        processExternalLink(request, requestedState);
    }

    @Inject
    protected void setApplicationContext(ApplicationContext applicationContext) {
        Dialogs dialogs = new WebDialogs(this);
        autowireContext(dialogs, applicationContext);
        setDialogs(dialogs);

        Notifications notifications = new WebNotifications(this);
        autowireContext(notifications, applicationContext);
        setNotifications(notifications);

        WebBrowserTools webBrowserTools = new WebBrowserToolsImpl(this);
        autowireContext(webBrowserTools, applicationContext);
        setWebBrowserTools(webBrowserTools);

        Fragments fragments = new WebFragments(this);
        autowireContext(fragments, applicationContext);
        setFragments(fragments);

        Screens screens = new WebScreens(this);
        autowireContext(screens, applicationContext);
        setScreens(screens);

        UrlRouting urlRouting = new WebUrlRouting(this);
        autowireContext(urlRouting, applicationContext);
        setUrlRouting(urlRouting);

        History history = new WebHistory(this);
        autowireContext(history, applicationContext);
        setHistory(history);

        UrlChangeHandler urlChangeHandler = new UrlChangeHandler(this);
        autowireContext(urlChangeHandler, applicationContext);
        setUrlChangeHandler(urlChangeHandler);

        getPage().addPopStateListener(urlChangeHandler::handleUrlChange);
    }

    protected void autowireContext(Object instance, ApplicationContext applicationContext) {
        AutowireCapableBeanFactory autowireBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        autowireBeanFactory.autowireBean(instance);

        if (instance instanceof ApplicationContextAware) {
            ((ApplicationContextAware) instance).setApplicationContext(applicationContext);
        }

        if (instance instanceof InitializingBean) {
            try {
                ((InitializingBean) instance).afterPropertiesSet();
            } catch (Exception e) {
                throw new RuntimeException(
                        "Unable to initialize UI Component - calling afterPropertiesSet for " +
                                instance.getClass(), e);
            }
        }
    }

    protected boolean isUserSessionAlive(Connection connection) {
        try {
            UserSession session = connection.getSession();
            if (session instanceof ClientUserSession
                    && ((ClientUserSession) session).isAuthenticated()) {
                userSessionService.getUserSession(session.getId());
            }
            return true;
        } catch (NoUserSessionException e) {
            return false;
        }
    }

    protected boolean isUserSessionAlive() {
        return currentSession instanceof ClientUserSession
                && ((ClientUserSession) currentSession).isAuthenticated();
    }

    protected void publishAppInitializedEvent(App app) {
        events.publish(new AppInitializedEvent(app));
    }

    protected void showCriticalExceptionMessage(@SuppressWarnings("unused") Exception exception) {
        String initErrorCaption = messages.getMainMessage("app.initErrorCaption");
        String initErrorMessage = messages.getMainMessage("app.initErrorMessage");

        VerticalLayout content = new VerticalLayout();
        content.setMargin(false);
        content.setSpacing(false);
        content.setStyleName("c-init-error-view");
        content.setSizeFull();

        VerticalLayout errorPanel = new VerticalLayout();
        errorPanel.setStyleName("c-init-error-panel");
        errorPanel.setWidthUndefined();
        errorPanel.setMargin(false);
        errorPanel.setSpacing(true);

        Label captionLabel = new Label(initErrorCaption);
        captionLabel.setWidthUndefined();
        captionLabel.setStyleName("c-init-error-caption");
        captionLabel.addStyleName("h2");
        captionLabel.setValue(initErrorCaption);

        errorPanel.addComponent(captionLabel);

        Label messageLabel = new Label(initErrorCaption);
        messageLabel.setWidthUndefined();
        messageLabel.setStyleName("c-init-error-message");
        messageLabel.setValue(initErrorMessage);

        errorPanel.addComponent(messageLabel);

        Button retryButton = new Button(messages.getMainMessage("app.initRetry"));
        retryButton.setStyleName("c-init-error-retry");
        retryButton.addClickListener(event -> {
            // always restart UI
            String url = ControllerUtils.getLocationWithoutParams() + "?restartApp";
            getPage().open(url, "_self");
        });

        errorPanel.addComponent(retryButton);
        errorPanel.setComponentAlignment(retryButton, Alignment.MIDDLE_CENTER);

        content.addComponent(errorPanel);
        content.setComponentAlignment(errorPanel, Alignment.MIDDLE_CENTER);

        setContent(content);
    }

    protected void setupUI() throws LoginException {
        if (!app.getConnection().isConnected()) {
            app.loginOnStart();
        } else {
            app.createTopLevelWindow(this);
        }
    }

    @Override
    protected void refresh(VaadinRequest request) {
        super.refresh(request);

        boolean sessionIsAlive = true;

        Connection connection = app.getConnection();

        if (connection.isAuthenticated()) {
            // Ping middleware session if connected
            log.debug("Ping middleware session");

            try {
                UserSession session = connection.getSession();
                if (session instanceof ClientUserSession
                        && ((ClientUserSession) session).isAuthenticated()) {
                    userSessionService.getUserSession(session.getId());

                }
            } catch (Exception e) {
                sessionIsAlive = false;

                app.exceptionHandlers.handle(new com.vaadin.server.ErrorEvent(e));
            }

            if (sessionIsAlive) {
                events.publish(new SessionHeartbeatEvent(app));
            }
        }

        if (sessionIsAlive) {
            events.publish(new UIRefreshEvent(this));
        }
    }

    @Override
    public void handleRequest(VaadinRequest request) {
        // on refresh page call
        processExternalLink(request, getUrlRouting().getState());
    }

    @Override
    public void changeVariables(Object source, Map<String, Object> variables) {
        super.changeVariables(source, variables);
    }

    /**
     * @return current AppUI
     */
    public static AppUI getCurrent() {
        return (AppUI) UI.getCurrent();
    }

    /**
     * @return this App instance
     */
    public App getApp() {
        return app;
    }

    /**
     * @return currently displayed top-level window
     */
    @Nullable
    public RootWindow getTopLevelWindow() {
        return topLevelWindow;
    }

    @Nonnull
    public RootWindow getTopLevelWindowNN() {
        if (topLevelWindow == null) {
            throw new IllegalStateException("UI yopLevelWindow is null");
        }

        return topLevelWindow;
    }

    /**
     * INTERNAL.
     * Set currently displayed top-level window.
     */
    public void setTopLevelWindow(RootWindow window) {
        if (this.topLevelWindow != window) {
            this.topLevelWindow = window;

            if (window != null) {
                setContent(topLevelWindow.unwrapComposition(Component.class));
            } else {
                setContent(null);
            }
        }
    }

    public TestIdManager getTestIdManager() {
        return testIdManager;
    }

    /**
     * @return true if UI test mode is enabled and cuba-id attribute should be added to DOM tree
     */
    public boolean isTestMode() {
        return testMode;
    }

    public boolean isPerformanceTestMode() {
        return performanceTestMode;
    }

    @Override
    public void error(com.vaadin.server.ErrorEvent event) {
        try {
            app.getExceptionHandlers().handle(event);
            app.getAppLog().log(event);
        } catch (Throwable e) {
            log.error("Error handling exception\nOriginal exception:\n{}\nException in handlers:\n{}",
                    ExceptionUtils.getStackTrace(event.getThrowable()),
                    ExceptionUtils.getStackTrace(e));
        }
    }

    protected void processExternalLink(VaadinRequest request, NavigationState requestedState) {
        if (isLinkHandlerRequest(request)) {
            processLinkHandlerRequest(request);
        } else {
            processRequest(requestedState);
        }
    }

    protected boolean isLinkHandlerRequest(VaadinRequest request) {
        WrappedSession wrappedSession = request.getWrappedSession();
        if (wrappedSession == null) {
            return false;
        }

        String action = (String) wrappedSession.getAttribute(LAST_REQUEST_ACTION_ATTR);

        return webConfig.getLinkHandlerActions().contains(action);
    }

    protected void processLinkHandlerRequest(VaadinRequest request) {
        WrappedSession wrappedSession = request.getWrappedSession();
        //noinspection unchecked
        Map<String, String> params =
                (Map<String, String>) wrappedSession.getAttribute(LAST_REQUEST_PARAMS_ATTR);
        params = params != null ? params : Collections.emptyMap();

        try {
            String action = (String) wrappedSession.getAttribute(LAST_REQUEST_ACTION_ATTR);
            LinkHandler linkHandler = AppBeans.getPrototype(LinkHandler.NAME, app, action, params);
            if (app.connection.isConnected() && linkHandler.canHandleLink()) {
                linkHandler.handle();
            } else {
                app.linkHandler = linkHandler;
            }
        } catch (Exception e) {
            error(new com.vaadin.server.ErrorEvent(e));
        }
    }

    protected void processRequest(NavigationState navigationState) {
        if (UrlHandlingMode.URL_ROUTES != webConfig.getUrlHandlingMode() || navigationState == null) {
            return;
        }

        if (!app.getConnection().isAuthenticated()) {
            RedirectHandler redirectHandler = beanLocator.getPrototype(RedirectHandler.NAME, this);
            redirectHandler.schedule(navigationState);
            app.redirectHandler = redirectHandler;
        } else {
            urlChangeHandler.getScreenNavigator()
                    .handleScreenNavigation(navigationState);
        }
    }

    @Override
    public void detach() {
        log.trace("Detaching UI {}", this);
        super.detach();
    }

    protected void updateClientSystemMessages(Locale locale) {
        SystemMessages msgs = new SystemMessages();

        msgs.communicationErrorCaption = messages.getMainMessage("communicationErrorCaption", locale);
        msgs.communicationErrorMessage = messages.getMainMessage("communicationErrorMessage", locale);

        msgs.sessionExpiredErrorCaption = messages.getMainMessage("sessionExpiredErrorCaption", locale);
        msgs.sessionExpiredErrorMessage = messages.getMainMessage("sessionExpiredErrorMessage", locale);

        msgs.authorizationErrorCaption = messages.getMainMessage("authorizationErrorCaption", locale);
        msgs.authorizationErrorMessage = messages.getMainMessage("authorizationErrorMessage", locale);

        updateSystemMessagesLocale(msgs);

        ReconnectDialogConfiguration reconnectDialogConfiguration = getReconnectDialogConfiguration();

        reconnectDialogConfiguration.setDialogText(messages.getMainMessage("reconnectDialogText", locale));
        reconnectDialogConfiguration.setDialogTextGaveUp(messages.getMainMessage("reconnectDialogTextGaveUp", locale));
    }

    protected void onHistoryBackPerformed() {
        Window topLevelWindow = getTopLevelWindow();
        if (topLevelWindow != null) {
            Screen frameOwner = topLevelWindow.getFrameOwner();
            if (frameOwner instanceof CubaHistoryControl.HistoryBackHandler) {
                ((CubaHistoryControl.HistoryBackHandler) frameOwner).onHistoryBackPerformed();
            }
        }
    }

    protected AbstractComponent getTopLevelWindowComposition() {
        if (topLevelWindow == null) {
            throw new IllegalStateException("UI does not have top level window");
        }

        return topLevelWindow.unwrapComposition(AbstractComponent.class);
    }

    public List<CubaTimer> getTimers() {
        AbstractComponent timersHolder = getTopLevelWindowComposition();

        return timersHolder.getExtensions().stream()
                .filter(extension -> extension instanceof CubaTimer)
                .map(extension -> (CubaTimer) extension)
                .collect(Collectors.toList());
    }

    public void addTimer(CubaTimer timer) {
        AbstractComponent timersHolder = getTopLevelWindowComposition();

        if (!timersHolder.getExtensions().contains(timer)) {
            timer.extend(timersHolder);
        }
    }

    public void removeTimer(CubaTimer timer) {
        AbstractComponent timersHolder = getTopLevelWindowComposition();

        timersHolder.removeExtension(timer);
    }

    public void beforeTopLevelWindowInit() {
        updateUiTheme();

        // todo move to login handling
        updateClientSystemMessages(app.getLocale());

        // todo move test id manager into RootWindow ?
        getTestIdManager().reset();
    }

    protected void updateUiTheme() {
        UserSession userSession = userSessionSource.getUserSession();

        if (userSession instanceof ClientUserSession && ((ClientUserSession) userSession).isAuthenticated()) {
            // load theme from user settings
            String themeName = userSettingsTools.loadAppWindowTheme();

            if (!Objects.equals(themeName, getTheme())) {
                // check theme support
                Set<String> supportedThemes = themeConstantsRepository.getAvailableThemes();
                if (supportedThemes.contains(themeName)) {
                    app.applyTheme(themeName);
                    setTheme(themeName);
                }
            }
        }
    }

    public CubaFileDownloader getFileDownloader() {
        return fileDownloader;
    }

    public UiEventsMulticaster getUiEventsMulticaster() {
        return uiEventsMulticaster;
    }

    @Override
    public Resource createVersionedResource(String value) {
        return iconResolver.getIconResource(value);
    }

    @Override
    public String getWebJarPath(String webjar, String partialPath) {
        return webJarResourceResolver.getWebJarPath(webjar, partialPath);
    }

    @Override
    public String translateToWebPath(String fullWebJarPath) {
        return webJarResourceResolver.translateToWebPath(fullWebJarPath);
    }

    @Override
    public void paintContent(PaintTarget target) throws PaintException {
        super.paintContent(target);

        /*if (isUserSessionAlive()
                && !Objects.equals(currentSession, app.getConnection().getSession())) {
            app.createTopLevelWindow(this);
        }*/

        String lastHistoryOp = ((WebUrlRouting) getUrlRouting()).getLastHistoryOperation();
        target.addAttribute(CubaUIConstants.LAST_HISTORY_OP, lastHistoryOp);
    }
}