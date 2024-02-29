Проект: Приложение для работы с новостями. 
Имеются следующие сущности: 
 - Journalist (журналист - автор новости), 
 - News (новость),
 - Author (автор комментария), 
 - Comment (комментарий к новости),
 - User (для авторизации, роли ADMIN, JOURNALIST, SUBSCRIBER)

В приложении имеются следующие микросервисы:
- EurekaServerApplication (для регистарции всех микросервисов)
- GatewayApplication (единая точка входа)
- SpringCloudConfigApplication (для вынесения конфигурации отдельно от микросервиса)
- SecurityApplication (для авторизации и идентификации пользователя)
- CommentsApplication (микросервис для комментариев)
- NewsApplication (микросервис для новостей)

Запуск приложения:
1. скачать проект;
2. в командной строке перейти в папку, где находится файл docker-compose.yml и выполнить команду docker-compose up -d
3. запустить в приложении все микросервисы.

Автоматически при запуске контейнера с postgres произойдет создание базы данных, а при запуске приложения - произойдет создание таблиц  (используется liquibase) и заполнение их первичными данными.

Первоначально для доступа к ресурсам приложения необходимо зарегистрироваться.
Имеется отдельный микросервис, который предоставляет возможности регистрации и получения токена для дальнейшей работы и доступа к ресурсам в зависимости от роли (ADMIN, JOURNALIST, SUBSCRIBER).
1. регистация пользователя (**POST** "http://localhost:8083/api/v1/auth/signup")
пример json: 
```
{
    "firstName": "Francesca",
    "lastName": "Petrova",
    "email": "francesca@gmail.com",
    "password": "12345"
}
```
2. после регистрации для получения токена необходимо сделать запрос
   (**POST** "http://localhost:8083/api/v1/auth/signin") с логином и паролем.
пример json:
```
   {
   "email": "francesca@gmail.com",
   "password": "12345"
   }
```
Далее полученный токен необходимо передавать в header "token" для доступа к ресурсам.
Ограничения spring security:
Администратор (role ADMIN) может производить CRUD-операции со всеми сущностями.
Журналист (role JOURNALIST) может добавлять и изменять/удалять только свои новости.
Подписчик (role SUBSCRIBER) может добавлять и изменять/удалять только свои комментарии.
Незарегистрированные пользователи могут только просматривать новости и комментарии.

Единая точка входа в приложение обеспечивает gateway ("http://localhost:8765/").
В приложении имеются следующие возможности работы с сущностью Journalist:
1. создание нового Journalist с помощью json (**POST** "news-service/api/v1/journalists");
   Пример Journalist в формате json:
```
   {
            "username": "anisa",
            "address": "25933 Stamm Fort, East Dwightshire, MO 84762",
            "email": "anisa@gmail.com",
            "telephone": "+375291234512"
        }
```
2. получение Journalist по id (**GET** "news-service/api/v1/journalists/{id}")
3. обновление Journalist по id (**PUT** "news-service/api/v1/journalists/{id}")
4. удаление Journalist по id (**DELETE** "news-service/api/v1/journalists/{id}")
5. получение Journalist с пагинацией (**GET** "news-service/api/v1/journalists"), с параментрами page - страница, size - количество House на странице (если не задано, по умолчению 15), orderBy - по какому полю сортировать (по умолчанию "username"), direction - как сотрировать (по умолчению "ASC")
6. блокировка Journalist по id (**POST** "news-service/api/v1/journalists/{id}") - заблокированный Journalist не может добавлять/редактировать свои новости 
7. поиск всех новостей Journalist по id (**POST** "news-service/api/v1/journalists/{id}/news")

В приложении имеются следующие возможности работы с сущностью News:
1. создание новой News с помощью json (**POST** "news-service/api/v1/news");
   Пример News в формате json:
```
{
        "title": "Gravida rutrum quisque non tellus.",
        "text": "Ut morbi tincidunt augue interdum velit euismod in pellentesque massa placerat duis ultricies lacus",
        "username": "savanna"
}
```
2. получение News по id (**GET** "news-service/api/v1/news/{id}")
3. обновление News по id (**PUT** "news-service/api/v1/news/{id}")
4. удаление News по id (**DELETE** "news-service/api/v1/news/{id}")
5. получение News с пагинацией (**GET** "news-service/api/v1/news"), с параментрами page - страница, pagesize - количество Person на странице (если не задано, по умолчению 15),  orderBy - по какому полю сортировать (по умолчанию "updateDate"), direction - как сотрировать (по умолчению "ASC")

В приложении имеются следующие возможности работы с Author:
1. создание нового Author с помощью json (**POST** "comments-service/api/v1/authors");
   Пример Author в формате json:
```
   {
            "username": "cecile",
            "address": "Apt. 491 490 McClure Meadows, South Willetta, TX 20977-9821",
            "email": "cecile@gmail.com",
            "telephone": "+375291234571"
        }
```
2. получение Author по id (**GET** "comments-service/api/v1/authors/{id}")
3. обновление Author по id (**PUT** "comments-service/api/v1/authors/{id}")
4. удаление Author по id (**DELETE** "comments-service/api/v1/authors/{id}")
5. получение Author с пагинацией (**GET** "comments-service/api/v1/authors"), с параментрами page - страница, size - количество House на странице (если не задано, по умолчению 15), orderBy - по какому полю сортировать (по умолчанию "username"), direction - как сотрировать (по умолчению "ASC")
6. блокировка Author по id (**POST** "comments-service/api/v1/authors/{id}") - заблокированный Author не может добавлять/редактировать свои комментарии.


В приложении имеются следующие возможности работы с Comment:
1. добавить Comment (**POST** "comments-service/api/v1/news/1/comments") где в запросе сразу передаем id новости, к которой будет принадледать комментарий
```
        {
            "text": "Orci nulla pellentesque dignissim enim sit amet venenatis urna cursus eget nunc scelerisque viverra mauris in aliquam sem fringilla ut morbi tincidunt augue interdum velit euismod in.",
            "authorName": "hassan"
        }
```
2. удалить Comment по id (**DELETE** "comments-service/api/v1/news/1/comments/{id}")
3. обновление Comment по id (**PUT** "comments-service/api/v1/news/1/comments/{id}")
4. удаление Comment по id (**DELETE** "comments-service/api/v1/news/1/comments/{id}")
5. получение Comment с пагинацией (**GET** "ccomments-service/api/v1/news/1/comments"), с параментрами page - страница, size - количество House на странице (если не задано, по умолчению 15), orderBy - по какому полю сортировать (по умолчанию "updateDate), direction - как сотрировать (по умолчению "ASC")

В приложении используется swagger, для доступа к докуметации к микросервису COMMENTS-SERVICE можно зайти на http://localhost:8082/swagger-ui/index.html#
Для доступа к докуметации к микросервису NEWS-SERVICE можно зайти на http://localhost:8081/swagger-ui/index.html#

В проекте использованы следующие технологии:
1. **Docker** - для развертывания приложения PostgreSQL, для работы с базой данных, автоматическая инициализация базы данных. Также поднимается образ с redis для кэширования данных приложения.
2. **Redis** - для кэширования данных приложения.
3. **Liquibase** - для отслеживания, управления и обеспечения изменений схемы базы данных.
4. **Gradle** - автоматизировать сборку проектов.
5. **Spring Boot 3.2.2** - создание серверной части приложения.
6. **Mapstruct** - для генерации кода для передачи данных между различными объектами в программе. Это помогает сопоставлять объекты из одной сущности в другую.
7. **Validation** - проверить правильность ввода пользовательских данных.
8. **Spring Security** - для обеспечения безопасного использования приложения. Предоставление прав и возможных действий с учетом роли, которую имеет пользователь.
9. **Swagger** - для документирования конечных точек приложения.
10. **Testing** - модульные тесты для тестирования уровня обслуживания и интеграционные тесты для тестирования уровня контроллера с использованием платформы Mockito, Testcontainers, WireMock.
11. **Logback** - отвечает за логирования информации на основе указанных уровней журнала. Основная задача журнала - не пропустить событие, которое необходимо записать в файл журнала.
12. **PostgreSQL** - база данных.
13. **Java 17** - версия Java.
14. **Spring Eureka Server** - для регистрации всех микросервисов.
15. **GateWay** - единая точка входа в приложение.
16. **Spring Cloud Config** - для хранения конфигурации отдельно от приложения.
17. **Spring Cloud Feign client** - для общения между микросервисами COMMENTS-SERVICE и NEWS-SERVICE.
18. **Github** - для размещения проекта и управления версиями проекта и его доработкой.