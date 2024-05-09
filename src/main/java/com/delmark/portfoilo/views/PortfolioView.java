package com.delmark.portfoilo.views;

import com.delmark.portfoilo.exceptions.*;
import com.delmark.portfoilo.models.*;
import com.delmark.portfoilo.models.DTO.PlacesOfWorkDto;
import com.delmark.portfoilo.models.DTO.PortfolioDto;
import com.delmark.portfoilo.models.DTO.ProjectsDto;
import com.delmark.portfoilo.repository.*;
import com.delmark.portfoilo.service.interfaces.PortfolioService;
import com.delmark.portfoilo.service.interfaces.ProjectService;
import com.delmark.portfoilo.service.interfaces.TechService;
import com.delmark.portfoilo.service.interfaces.WorkplacesService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;


import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static com.delmark.portfoilo.utils.DateUtils.convertToDateViaSqlDate;
import static com.delmark.portfoilo.utils.DateUtils.getDateString;

@Route(value = "/portfolio/:id", layout = MainLayout.class)
@PermitAll
public class PortfolioView extends VerticalLayout implements BeforeEnterObserver {

    private String portfolioId;
    private final PortfolioRepository portfolioRepository;
    private final PlacesOfWorkRepository placesOfWorkRepository;
    private final ProjectsRepository projectsRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final AuthenticationContext authenticationContext;
    private final TechService techService;
    private final WorkplacesService workplacesService;
    private final PortfolioService portfolioService;
    public PortfolioView(PortfolioRepository portfolioRepository,
                         PlacesOfWorkRepository placesOfWorkRepository,
                         ProjectsRepository projectsRepository,
                         UserRepository userRepository,
                         AuthenticationContext authenticationContext,
                         ProjectService projectService,
                         TechService techService,
                         WorkplacesService workplacesService,
                         PortfolioService portfolioService) {
        this.projectService = projectService;
        this.portfolioRepository = portfolioRepository;
        this.placesOfWorkRepository = placesOfWorkRepository;
        this.projectsRepository = projectsRepository;
        this.userRepository = userRepository;
        this.authenticationContext = authenticationContext;
        this.techService = techService;
        this.workplacesService = workplacesService;
        this.portfolioService = portfolioService;

        this.setHeight("100%");
    }

    private void createMainLayout() {
        com.delmark.portfoilo.models.Portfolio portfolio = portfolioRepository.findById(Long.parseLong(portfolioId)).get();
        VerticalLayout mainLayout = new VerticalLayout();
        H1 yourProfile = new H1("Ваше портфолио");
        Hr hr = new Hr();
        Hr hr2 = new Hr();
        mainLayout.add(yourProfile);
        mainLayout.setAlignSelf(Alignment.CENTER, yourProfile);
        mainLayout.add(createUserInfo(portfolio));
        mainLayout.add(hr);
        mainLayout.add(createProjectsAndWorkplacesLayout(portfolio));
        mainLayout.add(hr2);
        mainLayout.add(createTechStackLayout());
        mainLayout.setWidth("100%");
        mainLayout.setHeight("100%");
        add(mainLayout);
    }


    private VerticalLayout createTechStackLayout() {
        VerticalLayout techStackLayout = new VerticalLayout();
        H3 techStack = new H3("Мой стек технологий");
        techStackLayout.add(techStack);

        HorizontalLayout techWrapper = new HorizontalLayout();

        Set<Techs> techPool = portfolioRepository.findById(Long.parseLong(portfolioId)).get().getTechses();

        for (Techs tech : techPool) {
            Div techCard = new Div();
            techCard.addClassNames(
                    "bg-contrast-5",
                    LumoUtility.BorderRadius.MEDIUM,
                    LumoUtility.Padding.SMALL,
                    LumoUtility.Display.FLEX,
                    LumoUtility.FlexDirection.COLUMN)
            ;

            Span techName = new Span(tech.getTechName());
            techName.addClassNames("font-bold", LumoUtility.Margin.MEDIUM);

            techCard.add(techName);
            techWrapper.add(techCard);
        }

        techStackLayout.add(techWrapper);

        Portfolio portfolio = portfolioService.getPortfolio(Long.parseLong(portfolioId));

        if (portfolio.getUser().getUsername().equals(authenticationContext.getPrincipalName().get())) {
            Button addTechButton = new Button("Добавить технологию");
            addTechButton.addClickListener(e -> openAddTechDialog());
            techStackLayout.add(addTechButton);
        }

        return techStackLayout;
    }

    private void openAddTechDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeaderTitle("Добавить технологию");

        ListBox<Techs> techList = new ListBox<>();
        techList.setItems(techService.getTechList());
        techList.setRenderer(new ComponentRenderer<>((SerializableFunction<Techs, HorizontalLayout>) techs -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setSpacing(true);
            layout.setAlignItems(Alignment.CENTER);

            Icon icon = new Icon(VaadinIcon.CODE);
            icon.setColor(techs.getTechName().contains("Java") ? "green" : "blue");

            Span name = new Span(techs.getTechName());
            name.getStyle().set("font-weight", "bold");

            Span description = new Span(techs.getTechDesc());
            description.getStyle().set("color", "var(--lumo-tertiary-text-color)");

            layout.add(icon, name, description);
            return layout;
        }));

        Button addButton = new Button("Добавить");
        Button cancelButton = new Button("Отмена");
        cancelButton.addClickListener(e -> dialog.close());
        addButton.addClickListener(e -> {
            Techs selectedTech = techList.getValue();
            if (selectedTech != null) {
                try {
                    portfolioService.addTechToPortfolio(Long.parseLong(portfolioId), selectedTech.getId());
                    dialog.close();
                    UI.getCurrent().getPage().reload();
                }
                catch (TechAlreadyInPortfolioException ex) {
                    Notification.show("Технология уже добавлена в портфолио");
                }
            } else {
                Notification.show("Пожалуйста, выберите технологию из списка.");
            }
        });

    dialog.getFooter().add(addButton, cancelButton);

    VerticalLayout dialogLayout = new VerticalLayout(techList);
    dialogLayout.setSpacing(true);
    dialogLayout.setPadding(true);
    dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

    dialog.add(dialogLayout);
    dialog.open();
}



    private HorizontalLayout createUserInfo(com.delmark.portfoilo.models.Portfolio portfolio) {
        HorizontalLayout userInfoLayout = new HorizontalLayout();
        VerticalLayout userData = new VerticalLayout();

        Avatar avatar = new Avatar(portfolio.getName());
        H3 name = new H3(portfolio.getName() + " " + portfolio.getMiddleName() + " " + portfolio.getSurname());
        userData.add(avatar, name);
        userData.setWidth("50%");
        userData.setAlignItems(Alignment.CENTER);
        userData.setJustifyContentMode(JustifyContentMode.CENTER);
        userInfoLayout.add(userData);

        // Обо мне
        HorizontalLayout aboutUser = new HorizontalLayout();
        Div aboutUserContainer = new Div();
        aboutUserContainer.addClassNames(LumoUtility.Background.CONTRAST_5,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Margin.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );
        aboutUserContainer.add(new H4("Обо мне:"));
        aboutUserContainer.add(new Paragraph(portfolio.getAboutUser()));
        aboutUserContainer.add(new H4("Образование:"));
        aboutUserContainer.add(new Paragraph(portfolio.getEducation()));
        aboutUserContainer.setWidth("50%");

        // Контактная информация
        Div contactInfo = new Div();
        contactInfo.addClassNames(LumoUtility.Background.CONTRAST_5,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Margin.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM
        );
        contactInfo.add(new H4("Контактная информация:"));
        if (portfolio.getPhone() == null && portfolio.getEmail() == null) {
            contactInfo.add(new Paragraph("Нет контактной информации"));
        }
        else {
            if (portfolio.getPhone() != null) {
                contactInfo.add(new Paragraph("Телефон: " + portfolio.getPhone()));
            }
            if (portfolio.getEmail() != null) {
                contactInfo.add(new Paragraph("Email: " + portfolio.getEmail()));
            }
        }
        contactInfo.setWidth("50%");

        aboutUser.add(aboutUserContainer, contactInfo);

        aboutUser.setWidth("50%");
        userInfoLayout.add(aboutUser);

        userInfoLayout.setAlignItems(Alignment.CENTER);
        userInfoLayout.setWidth("100%");


        if (portfolio.getUser().getUsername().equals(authenticationContext.getPrincipalName().get())) {
            Div buttonLayout = new Div();
            buttonLayout.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Margin.MEDIUM);
            Button addTechButton = new Button("Редактировать портфолио");
            addTechButton.addClickListener(e -> openEditPortfolioDialog());
            buttonLayout.add(addTechButton);

            Button deletePortfolioBtn = new Button("Удалить портфолио", event -> {
                Long portfolioId = portfolioService.getPortfolioIdByUser(
                        ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername()
                );
                portfolioService.deletePortfolio(portfolioId);
                UI.getCurrent().navigate("/");
            });
            deletePortfolioBtn.addThemeVariants(ButtonVariant.LUMO_ERROR);
            buttonLayout.add(deletePortfolioBtn);
            userInfoLayout.add(buttonLayout);
        }

        return userInfoLayout;
    }

    private HorizontalLayout createProjectsAndWorkplacesLayout(com.delmark.portfoilo.models.Portfolio portfolio) {
        HorizontalLayout overall = new HorizontalLayout();
        overall.setMaxHeight("50%");
        overall.setWidth("100%");

        // Layout для проектов
        VerticalLayout projects = new VerticalLayout();
        H4 projectsH4 = new H4("Мои проекты");
        projectsH4.addClassNames(LumoUtility.AlignSelf.CENTER);
        VerticalLayout projectList = new VerticalLayout();
        projectList.addClassNames(LumoUtility.BorderRadius.SMALL, LumoUtility.BorderColor.CONTRAST);

        List<Projects> projectsList = projectsRepository.findAllByPortfolio(portfolio);

        if (projectsList.isEmpty()) {
            projectList.add(new Paragraph("У вас нет проектов."));
        }
        else {
            for (Projects project : projectsList) {
                Div projectDiv = getProjectCard(project);
                projectList.add(projectDiv);

            }
        }

        projects.add(projectsH4, projectList);

        if (portfolio.getUser().getUsername().equals(authenticationContext.getPrincipalName().get())) {
            Button addProject = new Button("Добавить проект");
            addProject.addClickListener(event -> {
                Dialog createProjectDialog = createProjectEditDialog();
                createProjectDialog.open();
            });
            addProject.addClassNames(LumoUtility.AlignSelf.CENTER);
            projects.add(addProject);
        }

        projectList.setAlignSelf(Alignment.CENTER);
        projectList.setAlignItems(Alignment.CENTER);

        Scroller scroller = new Scroller(projects);
        scroller.setWidth("50%");
        scroller.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        overall.add(scroller);

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.addClassNames(LumoUtility.AlignItems.CENTER);
        VerticalLayout acrdionLayout = new VerticalLayout();
        acrdionLayout.addClassNames(LumoUtility.AlignItems.CENTER);
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
        wrapper.addClassNames(LumoUtility.AlignItems.CENTER, LumoUtility.JustifyContent.CENTER);

        if (portfolio.getUser().getUsername().equals(authenticationContext.getPrincipalName().get())) {
            Button addWorkPlaceButton = new Button("Добавить место работы");
            addWorkPlaceButton.addClassName("add-work-place-button");
            addWorkPlaceButton.addClickListener(e -> openAddWorkPlaceDialog());
            wrapper.add(addWorkPlaceButton);
        }

        Scroller scrollerWork = new Scroller(wrapper);
        scrollerWork.setWidth("50%");
        scrollerWork.setScrollDirection(Scroller.ScrollDirection.VERTICAL);
        overall.add(scrollerWork);


        return overall;
    }

    private void openAddWorkPlaceDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setHeaderTitle("Добавить место работы");

        TextField workplaceName = new TextField("Название места работы");
        TextArea workplaceDesc = new TextArea("Описание");
        TextField post = new TextField("Должность");
        DatePicker hireDate = new DatePicker("Дата приема на работу");
        DatePicker fireDate = new DatePicker("Дата увольнения");

        Button saveButton = new Button("Сохранить");
        Button cancelButton = new Button("Отмена");

        cancelButton.addClickListener(e -> dialog.close());
        saveButton.addClickListener(e -> {
            workplacesService.addWorkplaceToPortfolio(
                    Long.parseLong(portfolioId),
                    new PlacesOfWorkDto(
                            workplaceName.getValue(),
                            workplaceDesc.getValue(),
                            post.getValue(),
                            convertToDateViaSqlDate(hireDate.getValue()),
                            convertToDateViaSqlDate(fireDate.getValue())
                    ));
            dialog.close();
            UI.getCurrent().getPage().reload();
        });

        HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.setSpacing(true);

        VerticalLayout dialogLayout = new VerticalLayout(workplaceName, workplaceDesc, post, hireDate, fireDate, buttonLayout);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private static Div getProjectCard(Projects project) {
        Div projectDiv = new Div();
        Span projectNameSpan = new Span(project.getProjectName());
        Span projectDescSpan = new Span(project.getProjectDesc());
        projectDiv.add(projectNameSpan, projectDescSpan);
        if (!(project.getProjectLink() == null)) {
            Anchor projectLink = new Anchor(project.getProjectLink(), project.getProjectLink());
            projectDiv.add(projectLink);
        }
        projectDiv.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN, LumoUtility.Whitespace.NORMAL);
        projectDiv.addClassNames(LumoUtility.BorderRadius.SMALL, LumoUtility.Background.CONTRAST_5);
        projectDiv.setWidth("70%");
        projectDiv.addClassNames(LumoUtility.AlignItems.CENTER);
        return projectDiv;
    }

    Dialog createProjectEditDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(true);

        dialog.setHeaderTitle("Добавить новый проект");

        Div errorDiv = new Div();
        errorDiv.setVisible(false);
        errorDiv.addClassNames(LumoUtility.TextColor.ERROR, LumoUtility.AlignItems.CENTER);
        errorDiv.add(new Span("Ошибка! Введенные данные некорректны."));
        dialog.add(errorDiv);

        FormLayout formLayout = new FormLayout();
        TextField projectName = new TextField("Название проекта");
        TextArea projectDesc = new TextArea("Описание проекта");
        TextField projectLink = new TextField("Ссылка на проект");
        formLayout.add(projectName, projectDesc, projectLink);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );
        dialog.add(formLayout);

        Button saveButton = new Button("Сохранить", event -> {
            if (projectName.getValue().isEmpty() && projectDesc.getValue().isEmpty()) {
                errorDiv.setVisible(true);
                return;
            } else {
                projectService.addProjectToPortfolio(
                        Long.parseLong(portfolioId),
                        new ProjectsDto(
                                projectName.getValue(),
                                projectDesc.getValue(),
                                projectLink.getValue()
                        )
                );
                dialog.close();
                UI.getCurrent().getPage().reload();
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancelButton = new Button("Отмена", event -> dialog.close());

        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialog.getFooter().add(saveButton, cancelButton);
        return dialog;
    }

    private void openEditPortfolioDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");
        dialog.setHeaderTitle("Редактировать портфолио");

        Portfolio portfolio = portfolioRepository.findById(Long.parseLong(portfolioId)).get();

        TextField firstName = new TextField("Имя");
        firstName.setValue(portfolio.getName());

        TextField lastName = new TextField("Фамилия");
        lastName.setValue(portfolio.getSurname());

        TextField middleName = new TextField("Отчество");
        middleName.setValue(portfolio.getMiddleName());

        TextField education = new TextField("Отчество");
        education.setValue(portfolio.getEducation());

        EmailField email = new EmailField("Email");
        email.setValue(portfolio.getEmail());

        TextArea about = new TextArea("О себе");
        about.setValue(portfolio.getAboutUser());
        about.setHeight("150px");

        TextField phone = new TextField("Телефон");
        phone.setValue(portfolio.getPhone());

        TextField siteUrl = new TextField("Сайт");
        siteUrl.setValue(portfolio.getSiteUrl());

        Button saveButton = new Button("Сохранить");
        Button cancelButton = new Button("Отмена");

        cancelButton.addClickListener(e -> dialog.close());
        saveButton.addClickListener(e -> {
            portfolioService.portfolioEdit(
                    Long.parseLong(portfolioId),
                    new PortfolioDto(
                            firstName.getValue(),
                            lastName.getValue(),
                            middleName.getValue(),
                            about.getValue(),
                            education.getValue(),
                            email.getValue(),
                            phone.getValue(),
                            siteUrl.getValue()
                    ));
            dialog.close();
            UI.getCurrent().getPage().reload();
        });

        dialog.getFooter().add(saveButton, cancelButton);


        VerticalLayout dialogLayout = new VerticalLayout(firstName, lastName, middleName, education, email, about, phone, siteUrl);
        dialogLayout.setSpacing(true);
        dialogLayout.setPadding(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);

        dialog.add(dialogLayout);
        dialog.open();
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
                    // Заменяем никнейм пользователя на id портфолио
                    portfolioId = String.valueOf(
                            portfolioRepository.findByUser(userRepository.
                                    findByUsername(portfolioId).
                                    orElseThrow(UserNotFoundException::new)).
                                    orElseThrow(UserDoesNotHavePortfolioException::new).getId()
                    );
                } catch (UserDoesNotHavePortfolioException e) {
                    if (authenticationContext.getPrincipalName().get().equals(portfolioId)) {
                        // TODO: Реализовать отправку на создание портфолио
                        event.rerouteTo(PortfolioCreationView.class);
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
