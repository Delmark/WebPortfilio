package com.delmark.portfoilo.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.security.AuthenticationContext;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Route("logout")
@PermitAll
public class LogoutView extends HorizontalLayout implements BeforeEnterObserver {

    AuthenticationContext authenticationContext;

    public LogoutView(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (authenticationContext.isAuthenticated()) {
            UI.getCurrent().getPage().setLocation("/");
            SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
            logoutHandler.logout(
                    VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                    null);
        }
        else {
            UI.getCurrent().getPage().setLocation("/");
        }
    }
}
