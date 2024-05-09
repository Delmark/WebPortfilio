package com.delmark.portfoilo.views;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.spring.security.AuthenticationContext;

public class PortfolioCreationView extends VerticalLayout implements BeforeEnterObserver {

    AuthenticationContext authenticationContext;

    public PortfolioCreationView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }
}
