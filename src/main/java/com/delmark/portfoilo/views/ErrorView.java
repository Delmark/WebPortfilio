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

    private String statusCode;
    private String message;

    public ErrorView() {
        H1 h1 = new H1("Ошибка! Код: " + statusCode);
        H2 h2 = new H2(message);
        setAlignSelf(Alignment.CENTER, h1, h2);
        setAlignItems(Alignment.CENTER);
        add(h1, h2);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        statusCode = beforeEnterEvent.getRouteParameters().get("status").orElse("500");

        switch (statusCode) {
            case "404" -> message = "Мы ничего не нашли :(";
            case "500" -> message = "Внутреняя ошибка сервера";
            case "403" -> message = "Доступ запрещён";
            default -> {
                statusCode = "500";
                message = "Внутреняя ошибка сервера";
            }
        }
    }
}
