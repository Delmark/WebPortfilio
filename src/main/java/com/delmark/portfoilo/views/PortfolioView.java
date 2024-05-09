package com.delmark.portfoilo.views;

import com.delmark.portfoilo.exceptions.NoSuchPortfolioException;
import com.delmark.portfoilo.exceptions.UserNotFoundException;
import com.delmark.portfoilo.models.PlacesOfWork;
import com.delmark.portfoilo.models.Projects;
import com.delmark.portfoilo.repository.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;


import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.delmark.portfoilo.utils.DateUtils.getDateString;

@Route(value = "/portfolio/:id", layout = MainLayout.class)
@PermitAll
public class PortfolioView extends VerticalLayout implements BeforeEnterObserver {

    private String portfolioId;
    private final PortfolioRepository portfolioRepository;
    private final PlacesOfWorkRepository placesOfWorkRepository;
    private final ProjectsRepository projectsRepository;
    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;
    public PortfolioView(PortfolioRepository portfolioRepository, PlacesOfWorkRepository placesOfWorkRepository, ProjectsRepository projectsRepository, UserRepository userRepository, AuthenticationContext authenticationContext) {
        this.portfolioRepository = portfolioRepository;
        this.placesOfWorkRepository = placesOfWorkRepository;
        this.projectsRepository = projectsRepository;
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;

        this.setHeight("100%");
    }

    private void createMainLayout() {
        com.delmark.portfoilo.models.Portfolio portfolio = portfolioRepository.findById(Long.parseLong(portfolioId)).get();
        VerticalLayout mainLayout = new VerticalLayout();
        H1 yourProfile = new H1("Ваше портфолио");
        Hr hr = new Hr();
        mainLayout.add(yourProfile);
        mainLayout.setAlignSelf(Alignment.CENTER, yourProfile);
        mainLayout.add(createUserInfo(portfolio));
        mainLayout.add(hr);
        mainLayout.add(createProjectsAndWorkplacesLayout(portfolio));
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        add(mainLayout);
    }

    private HorizontalLayout createUserInfo(com.delmark.portfoilo.models.Portfolio portfolio) {
        HorizontalLayout userInfoLayout = new HorizontalLayout();
        VerticalLayout userData = new VerticalLayout();

        Avatar avatar = new Avatar(portfolio.getName());
        H3 name = new H3(portfolio.getName() + " " + portfolio.getMiddleName() + " " + portfolio.getSurname());
        userData.add(avatar, name);
        userData.setWidth("50%");
        userInfoLayout.add(userData);

        Details aboutUser = new Details();
        aboutUser.setOpened(true);
        aboutUser.setSummaryText(portfolio.getAboutUser());
        aboutUser.setWidth("50%");
        userInfoLayout.add(aboutUser);

        userInfoLayout.setAlignItems(Alignment.CENTER);
        userInfoLayout.setWidth("100%");

        return userInfoLayout;
    }

    private HorizontalLayout createProjectsAndWorkplacesLayout(com.delmark.portfoilo.models.Portfolio portfolio) {
        HorizontalLayout overall = new HorizontalLayout();
        overall.setMaxHeight("50%");
        overall.setWidth("100%");

        // Layout для проектов
        VerticalLayout projects = new VerticalLayout();
        H4 projectsH4 = new H4("Ваши проекты");
        VerticalLayout projectList = new VerticalLayout();
        projectList.addClassNames(LumoUtility.BorderRadius.SMALL, LumoUtility.BorderColor.CONTRAST);

        List<Projects> projectsList = projectsRepository.findAllByPortfolio(portfolio);

        if (projectsList.isEmpty()) {
            projectList.add(new Paragraph("У вас нет проектов."));
        }
        else {
            // TODO: Сделать дизайн для карточек
            for (Projects project : projectsList) {
                Div projectDiv = new Div();
                Span projectNameSpan = new Span(project.getProjectName());
                Span projectDescSpan = new Span(project.getProjectDesc());
                projectDiv.add(projectNameSpan, projectDescSpan);
                if (!(project.getProjectLink() == null)) {
                    Span projectLink = new Span(project.getProjectLink());
                    projectDiv.add(projectLink);
                }
                projectDiv.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Whitespace.NORMAL);
                projectList.add(projectDiv);
            }
        }


        projects.add(projectsH4, projectList);
        projectList.setAlignSelf(Alignment.CENTER);

        Scroller scroller = new Scroller(projects);
        scroller.setWidth("50%");
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        overall.add(scroller);

        VerticalLayout wrapper = new VerticalLayout();
        VerticalLayout acrdionLayout = new VerticalLayout();
        // Layout для мест работы
        Accordion accordion = new Accordion();
        List<PlacesOfWork> workplaces = placesOfWorkRepository.findAllByPortfolioId(Long.parseLong(portfolioId));

        if (workplaces.isEmpty()) {
            accordion.add(new AccordionPanel("У вас нет мест работы :("));
        }
        else {
            for (PlacesOfWork workplace : workplaces) {
                Div workCard = new Div();
                workCard.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN);
                String workplaceName = "Место работы: " + workplace.getWorkplaceName();
                String workplaceDesc = workplace.getWorkplaceDesc();
                String post = "Должность: " + ((workplace.getPost() == null) ? "N/A" : workplace.getPost());
                String timeOfWork = ((workplace.getHireDate() == null) ? "N/A" : getDateString(workplace.getHireDate())) +
                        " - " + ((workplace.getFireDate() == null) ? "N/A" : getDateString(workplace.getFireDate()));
                workCard.add(
                        new Span(workplaceName),
                        new Span(workplaceDesc),
                        new Span(post),
                        new Span(timeOfWork)
                );
                accordion.add(workplace.getWorkplaceName(), workCard);
            }
        }
        acrdionLayout.add(accordion);
        wrapper.add(new H4("Ваши места работы"), acrdionLayout);
        Scroller scrollerWork = new Scroller(wrapper);
        scroller.setWidth("50%");
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        overall.add(scrollerWork);


        return overall;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<String> portfolioOptionalId = event.getRouteParameters().get("id");

        if (portfolioOptionalId.isPresent()) {
            portfolioId = portfolioOptionalId.get();
        }
        else {
            event.rerouteTo(ErrorView.class, new RouteParameters("status", "404"));
            return;
        }

        try {
            if (userRepository.existsByUsername(portfolioId)) {
                // В данном случае - индентификатор - имя пользователя
                try {
                    portfolioId = String.valueOf(
                            portfolioRepository.findByUser(userRepository.
                                    findByUsername(portfolioId).
                                    orElseThrow(UserNotFoundException::new)).
                                    orElseThrow(NoSuchPortfolioException::new).getId()
                    );
                } catch (NoSuchPortfolioException e) {
                    if (authenticationContext.getPrincipalName().get().equals(portfolioId)) {
                        // TODO: Реализовать отправку на создание портфолио
                        event.rerouteTo(ErrorView.class, new RouteParameters("status", "404"));
                        return;
                    }
                } catch (UserNotFoundException e) {
                    throw new IllegalArgumentException();
                }
            } else {
                if (!portfolioRepository.existsById(Long.parseLong(portfolioId))) {
                    throw new IllegalArgumentException();
                }
            }
        }
        catch(IllegalArgumentException e){
            event.rerouteTo(ErrorView.class, new RouteParameters("status", "404"));
            return;
        }

        createMainLayout();
    }
}
