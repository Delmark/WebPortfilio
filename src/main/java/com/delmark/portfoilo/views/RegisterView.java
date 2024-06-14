package com.delmark.portfoilo.views;

import com.delmark.portfoilo.exceptions.response.UsernameAlreadyExistsException;
import com.delmark.portfoilo.models.DTO.UserRegDTO;
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
import com.vaadin.flow.component.shared.HasValidationProperties;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldBase;
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

    private TextField username = new TextField("Логин", "Ваш логин");
    private PasswordField passwordField = new PasswordField("Пароль", "Ваш пароль");
    private TextField email = new TextField("Email", "Ваш Email");
    private TextField name = new TextField("Имя", "Ваше имя");
    private TextField surname = new TextField("Фамилия", "Ваша фамилия");
    private TextField middleName = new TextField("Отчество", "Ваше отчество");
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

        VerticalLayout wrapper = new VerticalLayout(createRegisterForm());
        wrapper.addClassNames(LumoUtility.JustifyContent.CENTER);
        wrapper.setWidth("90%");

        this.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW, LumoUtility.JustifyContent.CENTER);

        wrapper.add(button);
        registerFormLayout.add(wrapper);
        button.addClickListener(this::registerUser);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClassNames(LumoUtility.AlignSelf.CENTER);
        button.setWidth("40%");
        registerFormLayout.setAlignItems(Alignment.CENTER);
        registerFormLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        registerFormLayout.setHeight("70%");
        registerFormLayout.setWidth("40%");
        registerFormLayout.addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Margin.AUTO, LumoUtility.BorderRadius.LARGE);
        setWidth("100%");
        setHeight("100%");
        add(registerFormLayout);
    }

    private FormLayout createRegisterForm() {
        FormLayout formLayout = new FormLayout();

        username.setRequired(true);
        username.setMinLength(5);
        username.setMaxLength(32);
        passwordField.setRequired(true);
        passwordField.setMinLength(5);
        passwordField.setMaxLength(32);
        email.setRequired(true);
        name.setRequired(true);
        name.setMinLength(5);
        name.setMaxLength(32);
        surname.setRequired(true);
        surname.setMinLength(5);
        surname.setMaxLength(32);

        formLayout.add(username, passwordField, email, name, surname, middleName);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        return formLayout;
    }

    private void registerUser(ClickEvent<Button> event) {

        List<TextFieldBase> fieldsForValidation = List.of(username, passwordField, email, name, surname, middleName);

        if (fieldsForValidation.stream().anyMatch(HasValidationProperties::isInvalid)) {
            Map<String, List<String>> query = new HashMap<>();
            query.put("error", new ArrayList<>());
            UI.getCurrent().navigate("register", new QueryParameters(query));
            return;
        }

        try {
            userService.registration(
                    new UserRegDTO(
                            username.getValue(),
                            passwordField.getValue(),
                            name.getValue(),
                            surname.getValue(),
                            middleName.getValue(),
                            email.getValue()
                    )
            );
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
