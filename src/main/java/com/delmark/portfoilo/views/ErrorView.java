package com.delmark.portfoilo.views;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("error/:status")
@AnonymousAllowed
public class ErrorView extends VerticalLayout implements BeforeEnterObserver {

    H1 statusCode = new H1("Ошибка! Код: ");
    H2 errorMessage = new H2();

    public ErrorView() {
        setAlignSelf(Alignment.CENTER);
        setAlignItems(Alignment.CENTER);
        add(statusCode, errorMessage);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        statusCode.setText(beforeEnterEvent.getRouteParameters().get("status").orElse("500"));

        switch (statusCode.getText()) {
            case "404" -> errorMessage.setText("Мы ничего не нашли :(");
            case "500" -> errorMessage.setText("Внутреняя ошибка сервера");
            case "403" -> errorMessage.setText("Доступ запрещён");
            default -> {
                statusCode.setText("500");
                errorMessage.setText("Внутреняя ошибка сервера");
            }
        }
    }
}
