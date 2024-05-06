package com.delmark.portfoilo.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.lineawesome.LineAwesomeIcon;


public class MainLayout extends AppLayout {

    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
    }

    private void addDrawerContent() {
        H1 appName = new H1("DelmFolio");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNav());

        addToDrawer(header, scroller);
    }

    private SideNav createNav() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Главная", (String) null, LineAwesomeIcon.HOME_SOLID.create()));
        if (!authenticationContext.isAuthenticated()) {
            nav.addItem(new SideNavItem("Войти", LoginView.class, LineAwesomeIcon.DOOR_OPEN_SOLID.create()));
        }
        else {
            SideNavItem settingsNavItem = new SideNavItem("Настройки", (String) null, LineAwesomeIcon.WRENCH_SOLID.create());
            SideNavItem exitNavItem = new SideNavItem("Выйти", (String) null, LineAwesomeIcon.SIGN_OUT_ALT_SOLID.create());
            nav.addItem(new SideNavItem("Моё портфолио", "/portfolio/" + authenticationContext.getPrincipalName().get(), LineAwesomeIcon.TOOLBOX_SOLID.create()));
            nav.addItem(new SideNavItem("Другие пользователи", TestView.class,
                    LineAwesomeIcon.USER_FRIENDS_SOLID.create()));
            nav.addItem(settingsNavItem);
            nav.addItem(exitNavItem);

        }

        nav.setCollapsible(true);
        nav.setHeightFull();
        return nav;
    }
}
