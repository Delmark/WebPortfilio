# Веб-сервис для создания и просмотра портфолио

## Описание
Данный проект представляет собой веб-сервис, который позволяет создавать и просматривать портфолио. Портфолио представляют из себя набор информации о пользователе, его контакты, образование, личные проекты, места рабты и стек технологий которыми он владеет.

В данном проекте реализован как веб-сайт, так и REST API, с авторизацией на базе JWT токенов.

## Технологии: 

### Backend
- Java, 
- Spring Boot 
- Spring Security
- JPA
- PostgreSQL
### Frontend
- Vaadin

## Frontend

![Портфолио](https://github.com/Delmark/WebPortfolio/assets/102899273/d9205a60-6744-4b8a-b330-fd1cbf9f832c)
![Добавление технологии в стек](https://github.com/Delmark/WebPortfolio/assets/102899273/6c86e6fc-81ed-48e2-a16d-2f7277c5233b)
![Добавление технологии](https://github.com/Delmark/WebPortfolio/assets/102899273/92a85a42-c3cd-48a9-b379-8f55f69f61bf)


## Rest API Эндпойнты

### Доступные анонимным пользователям:

```
- POST /api/auth/register - Регистрация пользователя
- GET /api/auth/getToken - Получение JWT токена, для использования в последующих запросах
```
Оба эндпойнта принимают следующий JSON в теле запроса:
```
    {
      "username": string,
      "password": string
    }
```

### Доступные авторизованным пользователям:
#### Портфолио:
```
- GET /api/portfolio/{username} - Получение информации о портфолио пользователя
- GET /api/portfolio/id/{id} - Получение информации о портфолио по его ID
- POST /api/portfolio - Создание портфолио
- PUT /api/portfolio/tech?portId={id}&techId={id} - Добавление технологий в портфолио
- PUT /api/portfolio?id={id} - Редактирование портфолио
- DELETE /api/portfolio/{id} - Удаление портфолио
```
Запросы на создание, редактирование портфолио принимают следующий JSON в теле запроса:
```
{
  "name": string,
  "surname": string,
  "middleName": string,
  "aboutUser": string,
  "education": string,
  "email": string,
  "phone": string,
  "siteUrl": string
}
```
#### Проекты
```
- GET /api/projects?portfolioId={id} - Получение всех проектов из портфолио
- GET /api/projects/id/{id} - Получение проекта по ID
- POST /api/projects?portfolioId={id} - Добавление проекта в портфолио
- PUT /api/projects?projectId={id} - Редактирование информации о проекте
- DELETE /api/deleteProject?projectId={id} - Удаление проекта
```
Запросы на создание и редактирование принимают следующий JSON:
```
{
    "projectName": string,
    "projectDesc": string,
    "projectLink": string
}
```
#### Места работы:
```
- GET /api/workPlaces?portfolioId={id} - Получение всех местработы из портфолио
- GET /api/workPlaces/{id} - Получение конкретной записи о месте работы по Id
- POST /api/workPlaces?portfolioId={id} - Добавление места работы в портфолио
- PUT /api/workplaces?workplaceId={id} - Редактирование информацию о месте работы
- DELETE /api/workplace?workplaceId={id} - Удаление информации о месте работы
```
Запросы на создание и редактирование принимают следующий JSON в теле запроса:
```
{
    "workplaceName": string,
    "workplaceDesc": string,
    "post": string,
    "hireDate": string,
    "fireDate": string
}
```
#### Технологии
```
- GET /api/tech?page={page} - Получение страницы с всеми технологиями
- GET /api/tech/{id} - Получение технологии по ID
```
### Доступные администраторам:
```
- GET /api/tech?page={page} - Получение страницы с всеми технологиями
- GET /api/tech/{id} - Получение технологии по ID
- POST /api/tech - Создание новой технологии
- PUT /api/tech/{id} - Редактирование технологии
- DELETE /api/tech/{id} - Удаление технологии
```
Запросы на создание и редактирование принимают следующий JSON в теле запроса:
```
{
    "techName": string,
    "techDesc": string,
}
```

Проект выполнен Delmark :tm: a.k.a Алексеем Кругловым.
