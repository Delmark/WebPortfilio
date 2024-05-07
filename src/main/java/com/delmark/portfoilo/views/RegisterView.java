package com.delmark.portfoilo.views;

import com.delmark.portfoilo.exceptions.UsernameAlreadyExistsException;
import com.delmark.portfoilo.models.DTO.UserDto;
import com.delmark.portfoilo.service.interfaces.UserService;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Route(value = "register", layout = MainLayout.class)
@AnonymousAllowed
public class RegisterView extends HorizontalLayout implements BeforeEnterObserver {

    private UserService userService;
    private boolean error = false;
    private boolean userExistsError = false;

    private TextField username = new TextField("Логин", "Ваш логин");
    private PasswordField passwordField = new PasswordField("Пароль", "Ваш пароль");
    private Button button = new Button("Зарегистрироваться");
    private VerticalLayout registerFormLayout = new VerticalLayout();
    private Span errorSpan = new Span();
    private Div errorMessagediv;

    public RegisterView(UserService userService) {
        this.userService = userService;

        H1 registerH1 = new H1("Зарегистрироваться");
        registerFormLayout.add(registerH1);

        errorMessagediv = new Div();
        errorMessagediv.add(new Span("Ошибка!"));
        errorMessagediv.add(errorSpan);
        errorMessagediv.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.BorderColor.ERROR,
                LumoUtility.BorderRadius.MEDIUM
        );
        errorMessagediv.setVisible(false);

        registerFormLayout.add(errorMessagediv);

        HorizontalLayout wrapper = new HorizontalLayout(createRegisterForm());

        registerFormLayout.add(wrapper);
        registerFormLayout.add(button);
        button.addClickListener(this::registerUser);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerFormLayout.setAlignItems(Alignment.CENTER);
        registerFormLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        registerFormLayout.setHeight("100%");
        registerFormLayout.setWidth("100%");
        setWidth("100%");
        setHeight("100%");
        add(registerFormLayout);
    }

    private FormLayout createRegisterForm() {
        FormLayout formLayout = new FormLayout();

        formLayout.add(username, passwordField);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        return formLayout;
    }

    private void registerUser(ClickEvent<Button> event) {
        if ((username.isEmpty() && passwordField.isEmpty())) {
            Map<String, List<String>> query = new HashMap<>();
            query.put("error", new ArrayList<>());
            UI.getCurrent().navigate("register", new QueryParameters(query));
            return;
        }

        try {
            userService.registration(new UserDto(username.getValue(), passwordField.getValue()));
        }
        catch (UsernameAlreadyExistsException e) {
            Map<String, List<String>> query = new HashMap<>();
            query.put("userExists", new ArrayList<>());
            UI.getCurrent().navigate("register", new QueryParameters(query));
            return;
        }

        System.out.println("Регистрация завершена: " + username.getValue() + "  " + passwordField.getValue());
        UI.getCurrent().navigate(LoginView.class);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            errorSpan.setText("Некорректные данные!");
            errorMessagediv.setVisible(true);
        }
        else if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("userExists")) {
            errorSpan.setText("Такой пользователь уже существует!");
            errorMessagediv.setVisible(true);
        }
    }
}