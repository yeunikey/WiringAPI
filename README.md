<h1 align="center">Привет, я <a target="_blank">WiringAPI</a> 
<img src="https://github.com/blackcater/blackcater/raw/main/images/Hi.gif" height="32"/></h1>
<h3 align="center">Простой API для работы с базой данных MySQL с использованием HikariCP</h3>

<h3>Установка</h3>

Для работы с Maven:
```
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.xflyiwnl</groupId>
    <artifactId>WiringAPI</artifactId>
    <version>v1.0</version>
</dependency>
```

<h3>Начало работы</h3>

Перед начало работы с API, вам необходимо подключить его. Подключение к серверу происходит вот так:
```
WiringAPI api = new WiringAPI("localhost", "root", "1234");  
```
или так
```
WiringAPI api =  WiringAPI(String driver, String host, int port, String username, String password, Map<String, String> properties);
```
Здесь предоставлены все параметры для подключения к серверу. В `Map<String, String> properties` вы сможете добавить нужные настройки для конфига.

<h3>Примеры</h3>

<h4>Работа с базой данных</h4>

В нашем API база данных создаётся вот так:
```
api.createDatabase("database") // создаём базу данных с именем database
                .execute(); // выполняем запрос
```
Также, вы сможете проверить, существует ли база данных с названием `database`:
```
api.existsDatabase("database"); // вернёт вам true или false, если такой базы нет
```
Получение базы данных:
```
api.getDatabase("database"); // получим класс Database
```
и удаление:
```
api.getDatabase("database").drop();
```

<h4>Работа с таблицами</h4>

После создание базы данных, вы сможете создать таблицу данных в базе данных:
```
api.getDatabase("database") // получаем базу данных database
                .createTable("mytable") // создаём таблицу mytable
                .execute(); // выполняем запрос
```
Также, при создании мы сможем добавить в таблицу колонны:
```
.column(new Column("name", ColumnType.VARCHAR))
```
На примере этого создадим простую таблицу:
```
api.getDatabase("database")
                .createTable("mytable")
                .column(new Column("name", ColumnType.VARCHAR).primaryKey().notNull())
                .column(new Column("age", ColumnType.INT).notNull())
                .column(new Column("info", ColumnType.VARCHAR).notNull())
                .execute();
```
Создаст таблицу под названием `mytable` и с колоннами `name`, `age`, `info`. Колонна `name` является ключем и не может быть пустым, как и все колонны. Ключ нужен, чтобы получать нужные нам строки в таблице при работе с `.delete()`, `.insert()`, `.select()`.

Аналогично с базами данных у таблицы тоже есть методы для проверки, получения таблицы.

Существует ли таблица в базе данных `database`:
```
api.getDatabase("database").existsTable("mytable");
```

Получить таблицу в базе данных `database`:
```
api.getDatabase("database").getTable("mytable");
```
И удаление таблицы
```
api.getDatabase("database").getTable("mytable").drop();
```
Ошиблись в названии таблицы при создании?
```
api.getDatabase("database").getTable("mytable").renameTable("othertable");
```

<h4>Работа с колоннами</h4>

Создать колонну вы сможете вот так:
```
api.getDatabase("database").getTable("mytable")
                .createColumn(new Column("test", ColumnType.VARCHAR)); // создаёте ещё одну колонну под названием test и типом VARCHAR(255)
```
Получить все существующие колонны:
```
api.getDatabase("database").getTable("mytable").getColumns(); // вернёт вам массив классов с колоннами
```
И получить колонну по названию:
```
api.getDatabase("database").getTable("mytable").getColumn("test"); // вернёт вам класс колонны
```
Удаление колонны::
```
api.getDatabase("database").getTable("mytable").dropColumn("test");
```
Проверка колонны:
```
api.getDatabase("database").getTable("mytable").existsColumn("test");
```

<h4>Работа с insert</h4>

Создать запрос на внесение изменение в таблице можно так:
```
api.insert("database") // база данных с которым работаем
                .table("mytable") // таблица
                .column("name", "Yerassyl") 
                .column("age", 17)
                .column("info", "Разработчик")
                .execute(); // выполнение запроса
```

<h4>Работа с delete</h4>

Создать запрос на удаление строки в таблице:
```
api.delete("database") // база данных с которым работаем
                .table("mytable") // таблица
                .key("name") // по какому ключу мы сравниваем
                .value("Yerassyl") // значение этого ключа, чуть выше мы создали колонну name = Yerassyl
                .execute(); // создание запроса
```
Другой способ:
```
api.delete("database") // база данных с которым работаем
                .table("mytable") // таблица
                .value("Yerassyl") // значение этого ключа, чуть выше мы создали колонну name = Yerassyl
                .execute(); // создание запроса
```
Теперь ключ опеределяется автоматический, потому что при создании таблицы мы указали, что колонна `name` является ключем
```
.column(new Column("name", ColumnType.VARCHAR).primaryKey().notNull())
```

<h4>Работа с select</h4>

Создать запрос на получение строки из таблицы:
```
WiringResult result = api.select("database") // база данных с которым работаем
                .table("mytable") // таблица
                .key("name") // по какому ключу мы сравниваем
                .value("Yerassyl") // значение этого ключа, чуть выше мы создали колонну name = Yerassyl
                .execute(); // создание запроса
```
Другой способ без `key`:
```
WiringResult result = api.select("database") // база данных с которым работаем
                .table("mytable") // таблица
                .value("Yerassyl") // значение этого ключа, чуть выше мы создали колонну name = Yerassyl
                .execute(); // создание запроса
```
Теперь ключ опеределяется автоматический, потому что при создании таблицы мы указали, что колонна `name` является ключем

Класс `WiringResult` является упрощённым аналогом `ResultSet`. 

С его помощью вы сможете получить результат `select()` запроса.
```
result.getResult(); // вернёт вам Map<Column, Object> result. Класс колонны и что там записано. Например, класс колонны name и String Yerassyl.
```
Отформатированный `HashMap`:
```
result.formatted(); // вернёт вам Map<String, Object> result. Название колонны и что там записано. Например, название колонны name и String Yerassyl.
```
На этом примере мы сможем получить то, что записано в других колоннах:
```
result.get("name"); // вернёт вам Yerassyl
        result.get("age"); // вернёт вам 17
        result.get("info"); // вернёт вам Разработчик
```
Не забывайте, чтобы ответ к вам поступит в виде `Object`. Вам необходимо переделать его в `int`, `String` или в другой тип. Зависит от того, что там записано.
