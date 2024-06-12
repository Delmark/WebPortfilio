package com.delmark.portfoilo.views;

import com.delmark.portfoilo.exceptions.response.UserDoesNotHavePortfolioException;
import com.delmark.portfoilo.controller.requests.PortfolioRequest;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldBase;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.util.List;

@Route(value = "create/portfolio", layout = MainLayout.class)
@PermitAll
public class PortfolioCreationView extends VerticalLayout implements BeforeEnterObserver {

    AuthenticationContext authenticationContext;
    PortfolioService portfolioService;

    public PortfolioCreationView(AuthenticationContext authenticationContext,
                                 PortfolioService portfolioService) {
        this.authenticationContext = authenticationContext;
        this.portfolioService = portfolioService;

        add(createMainLayout());
    }

    VerticalLayout createMainLayout() {
        VerticalLayout mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        mainLayout.setPadding(false);
        mainLayout.setMargin(false);
        mainLayout.setSpacing(false);
        mainLayout.setAlignItems(Alignment.CENTER);
        mainLayout.setJustifyContentMode(JustifyContentMode.CENTER);

        Div creationForm = new Div();
        creationForm.addClassNames(
                LumoUtility.AlignItems.CENTER,
                LumoUtility.JustifyContent.CENTER,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.MEDIUM
        );
        creationForm.setWidth("60%");

        FormLayout formLayout = new FormLayout();

        // Создание полей для заполнения
        TextField education = new TextField("Образование");
        TextArea about = new TextArea("О себе");
        TextField phone = new TextField("Телефон");
        TextField siteUrl = new TextField("Сайт");

        // Валдиация на стороне Vaadin
        PortfolioView.setPortfolioValidationParams(education, about, phone);

        formLayout.add(education, about, phone, siteUrl);

        List<TextFieldBase> fields = List.of(education, about, phone, siteUrl);

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        creationForm.add(formLayout);
        Button button = new Button("Создать портфолио", event -> {

            boolean allFieldsIsValid = true;

            for (TextFieldBase field : fields) {
                if (field.isInvalid()) {
                    allFieldsIsValid = false;
                    break;
                }
            }

            if (allFieldsIsValid) {
                portfolioService.portfolioCreation(
                        new PortfolioRequest(
                                about.getValue(),
                                education.getValue(),
                                phone.getValue(),
                                siteUrl.getValue()
                        )
                );
                UI.getCurrent().navigate(PortfolioView.class,
                        new RouteParameters("id", authenticationContext.getPrincipalName().get())
                );
            }
            else {
                Notification.show("Введите корректные данные!");
            }
        }
        );

        mainLayout.add(creationForm, button);
        return mainLayout;
    }

    // Необхдимо проверить существует ли портфолио пользователя, если да, то перенаправить на портфолио
    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        try {
            portfolioService.getPortfolioByUser(authenticationContext.getPrincipalName().get());
            beforeEnterEvent.rerouteTo(PortfolioView.class,
                    new RouteParameters("id", authenticationContext.getPrincipalName().get())
            );
        }
        catch (UserDoesNotHavePortfolioException e) {
            return;
        }
    }
}
