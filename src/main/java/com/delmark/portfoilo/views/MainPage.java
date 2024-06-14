package com.delmark.portfoilo.views;

import com.delmark.portfoilo.exceptions.response.UserDoesNotHavePortfolioException;
import com.delmark.portfoilo.models.DTO.TechStatsProjection;
import com.delmark.portfoilo.models.DTO.WorkplacesStatsProjection;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import com.delmark.portfoilo.service.interfaces.TechService;
import com.delmark.portfoilo.service.interfaces.WorkplacesService;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@PageTitle("Главная")
@Route(value = "", layout = MainLayout.class)
@AnonymousAllowed
@Slf4j
@PermitAll
public class MainPage extends VerticalLayout implements BeforeEnterObserver {

    TechService techService;
    WorkplacesService workplacesService;
    AuthenticationContext authenticationContext;
    PortfolioService portfolioService;
    VerticalLayout layout;

    public MainPage(TechService techService,
                    WorkplacesService workplacesService,
                    AuthenticationContext authenticationContext,
                    PortfolioService portfolioService) {

        this.authenticationContext = authenticationContext;
        this.techService = techService;
        this.workplacesService = workplacesService;
        this.portfolioService = portfolioService;
        layout = new VerticalLayout();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.CENTER);
        H1 h1 = new H1("Delmark Web Portfolio");
        H2 h2 = new H2("Веб-приложение для просмотра и создания портфолио");

        layout.add(h1, h2);
        add(layout);
        add(new Hr());
        add(createStatsPage());
    }

    private VerticalLayout createStatsPage() {
        VerticalLayout statsPage = new VerticalLayout();
        statsPage.setAlignItems(Alignment.CENTER);
        statsPage.setJustifyContentMode(JustifyContentMode.CENTER);
        statsPage.add(new H2("Интересная статистика"));

        HorizontalLayout data = new  HorizontalLayout();
        // Отображение самых популярных технологий
        if (techService.getTechStatistics().isEmpty() && workplacesService.getStatistics().isEmpty()) {
            data.add(new H2("Нет данных для отображения"));
        }
        else {
            if (!techService.getTechStatistics().isEmpty()) {
                Chart chart = new Chart(ChartType.PIE);
                chart.addClassNames(
                        LumoUtility.Margin.MEDIUM
                );
                Configuration configuration = chart.getConfiguration();

                configuration.setTitle("Статистика по самым технологиям");
                configuration.setSubTitle("По упоминаниям в портфолио");

                PlotOptionsPie plotOptions = new PlotOptionsPie();
                plotOptions.setAllowPointSelect(true);
                plotOptions.setCursor(Cursor.POINTER);
                plotOptions.setShowInLegend(true);
                configuration.setPlotOptions(plotOptions);

                DataSeries dataSeries = new DataSeries();

                Optional<DataSeriesItem> mostUsedTechnology = Optional.empty();
                int maxCount = 0;

                for (TechStatsProjection dto : techService.getTechStatistics()) {
                    DataSeriesItem item = new DataSeriesItem(dto.getTechnology_name(), dto.getCount());
                    dataSeries.add(item);
                    if (dto.getCount() > maxCount) {
                        maxCount = dto.getCount();
                        mostUsedTechnology = Optional.of(item);
                    }
                }
                if (mostUsedTechnology.isPresent()) {
                    DataSeriesItem usedItem = mostUsedTechnology.get();
                    usedItem.setSliced(true);
                    usedItem.setSelected(true);
                }

                configuration.setSeries(dataSeries);
                data.add(chart);
            }
            if (!workplacesService.getStatistics().isEmpty()) {
                Chart chart = new Chart(ChartType.PIE);
                chart.addClassNames(
                        LumoUtility.Margin.MEDIUM
                );
                Configuration configuration = chart.getConfiguration();

                configuration.setTitle("Статистика по самым популярным компаниям");
                configuration.setSubTitle("По упоминаниям в портфолио");

                PlotOptionsPie plotOptions = new PlotOptionsPie();
                plotOptions.setAllowPointSelect(true);
                plotOptions.setCursor(Cursor.POINTER);
                plotOptions.setShowInLegend(true);
                configuration.setPlotOptions(plotOptions);

                DataSeries dataSeries = new DataSeries();

                Optional<DataSeriesItem> mostPopularCompany = Optional.empty();
                int maxCount = 0;

                for (WorkplacesStatsProjection dto : workplacesService.getStatistics()) {
                    DataSeriesItem item = new DataSeriesItem(dto.getWorkplaceName(), dto.getCount());
                    log.info("workplace name: " + dto.getWorkplaceName() + " count: " + dto.getCount());
                    dataSeries.add(item);
                    if (dto.getCount() > maxCount) {
                        maxCount = dto.getCount();
                        mostPopularCompany = Optional.of(item);
                    }
                }
                if (mostPopularCompany.isPresent()) {
                    DataSeriesItem usedItem = mostPopularCompany.get();
                    log.info("Most used workplace: " + usedItem);
                    usedItem.setSliced(true);
                    usedItem.setSelected(true);
                }

                configuration.setSeries(dataSeries);
                data.add(chart);
            }
        }

        statsPage.add(data);

        return statsPage;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        Button button = new Button();
        if (authenticationContext.isAuthenticated()) {
            try {
                portfolioService.getPortfolioByUser(authenticationContext.getPrincipalName().get());
                button.setText("Перейти к портфолио");
                button.addClickListener(event -> UI.getCurrent().navigate(PortfolioView.class, new RouteParameters("id", authenticationContext.getPrincipalName().get())));
            } catch (UserDoesNotHavePortfolioException e) {
                button.setText("Создать новое портфолио");
                button.addClickListener(event -> UI.getCurrent().navigate(PortfolioCreationView.class));
            }
        }
        else {
            button.setText("Войти");
            button.addClickListener(event -> UI.getCurrent().navigate(LoginView.class));
        }
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(button);
    }
}
