package com.delmark.portfoilo.views;

import com.delmark.portfoilo.models.DTO.authorization.JwtTokenDTO;
import com.delmark.portfoilo.models.userdata.User;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import com.delmark.portfoilo.service.interfaces.TokenService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "settings", layout = MainLayout.class)
@PermitAll
public class SettingsView extends VerticalLayout implements BeforeEnterObserver {

    // Компоненты необходимые как глобальные переменные
    Dialog modal = new Dialog();
    Span tokenSpan = new Span();
    HorizontalLayout deletePortfolioLayout;

    PortfolioService portfolioService;
    TokenService tokenService;
    AuthenticationContext authenticationContext;

    public SettingsView(PortfolioService portfolioService, TokenService tokenService, AuthenticationContext authenticationContext) {
        this.portfolioService = portfolioService;
        this.tokenService = tokenService;
        this.authenticationContext = authenticationContext;

        createDialog();
        add(createMainLayout());
    }

    private VerticalLayout createMainLayout() {

        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setHeight("100%");
        mainLayout.setWidth("100%");
        mainLayout.setAlignItems(Alignment.CENTER);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        VerticalLayout settingsLayout = new VerticalLayout();
        settingsLayout.setAlignItems(Alignment.CENTER);
        settingsLayout.setWidth("100%");
        settingsLayout.setWidth("100%");

        HorizontalLayout getTokenFieldLayout = new HorizontalLayout();
        H4 getTokenLabel = new H4("Получить JWT токен: ");
        Button getTokenButton = new Button("Получить JWT", (event) -> {
            JwtTokenDTO dto = tokenService.provideToken(SecurityContextHolder.getContext().getAuthentication());
            String token = dto.getToken();
            tokenSpan.setText(token);
            modal.open();
        });

        getTokenFieldLayout.setAlignItems(Alignment.CENTER);

        getTokenFieldLayout.add(getTokenLabel, getTokenButton);
        mainLayout.add(getTokenFieldLayout, new Hr());

        HorizontalLayout deletePortfolioLayout = getDeletePortfolioLayout();
        deletePortfolioLayout.setAlignItems(Alignment.CENTER);
        mainLayout.add(deletePortfolioLayout);

        return mainLayout;
    }

    private HorizontalLayout getDeletePortfolioLayout() {
        deletePortfolioLayout = new HorizontalLayout();
        H4 deletePortfolioLabel = new H4("Удалить портфолио: ");
        deletePortfolioLabel.addClassNames(LumoUtility.TextColor.WARNING);
        Button deletePortfolioBtn = new Button("Удалить портфолио", event -> {
            Long portfolioId = portfolioService.getPortfolioByUser(
                    ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()
            ).getId();
            portfolioService.deletePortfolio(portfolioId);
            UI.getCurrent().navigate("/");
        });
        deletePortfolioBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deletePortfolioLayout.add(deletePortfolioLabel, deletePortfolioBtn);
        deletePortfolioLayout.setVisible(false);
        deletePortfolioLayout.setEnabled(false);
        return deletePortfolioLayout;
    }

    // Создание модального окна для получения JWT токена
    private void createDialog() {
        modal.setHeaderTitle("JWT Токен");

        VerticalLayout modalLayout = new VerticalLayout();
        modalLayout.setAlignItems(Alignment.CENTER);
        modalLayout.add(new H4("Ваш JWT Token: "), tokenSpan);

        Button updateToken = new Button("Обновить токен", (event) -> {
            JwtTokenDTO dto = tokenService.provideToken(SecurityContextHolder.getContext().getAuthentication());
            String token = dto.getToken();
            tokenSpan.setText(token);
        });

        Button closeModal = new Button("Закрыть", event -> modal.close());

        modal.add(modalLayout);
        modal.getFooter().add(updateToken, closeModal);

        add(modal);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (portfolioService.portfolioExistsByUser(authenticationContext.getPrincipalName().get())) {
            deletePortfolioLayout.setVisible(true);
            deletePortfolioLayout.setEnabled(true);
        }
    }
}
