package com.delmark.portfoilo.views;

import com.delmark.portfoilo.models.userdata.User;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import jakarta.annotation.security.PermitAll;

@Route(value = "other", layout = MainLayout.class)
@PermitAll
public class OtherPeoplePortfolios extends VerticalLayout {

    PortfolioService portfolioService;
    UserService userService;

    public OtherPeoplePortfolios(PortfolioService portfolioService, UserService userService) {
        this.portfolioService = portfolioService;
        this.userService = userService;

        Grid<User> userGrid = new Grid<>(User.class, false);
        userGrid.addColumn(User::getUsername).setHeader("Пользователь");
        userGrid.addColumn(createButtonRenderer()).setHeader("Портфолио");

        userGrid.setItems(userService.getUsersWithPortfolio());
        add(userGrid);
    }

    private static Renderer<User> createButtonRenderer() {
        return LitRenderer.<User> of("<vaadin-button @click=${handleClick}>Посмотреть портфолио</vaadin-button>")
                .withFunction("handleClick", (User user) -> {
                    UI.getCurrent().navigate(PortfolioView.class, new RouteParameters("id", String.valueOf(user.getUsername())));
                });
    }
}
