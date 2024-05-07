package com.delmark.portfoilo.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.component.html.H1;

@PageTitle("Главная")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
@PermitAll
public class MainPage extends VerticalLayout {

    public MainPage() {
        Div div = new Div();
        H1 h1 = new H1("Delmark Web Portfolio");
        H2 h2 = new H2("Веб-приложение для просмотра и создания портфолио");
        Button button = new Button("Начать!");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        div.add(h1,h2,button);
        add(div);
        setAlignSelf(Alignment.CENTER,div);
    }
}
