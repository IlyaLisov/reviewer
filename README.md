# Reviewer
Website to find reviews on employees and education entities in Belarus.
Main goal of project is to bring useful information about education entities and their employees to students.

We are open to collaborating and making this project better.

https://t.me/webapp_reviewer - Telegram channel of this project.

####Before using you need to create database and put information in "application.properties" file.
This file should contains next data:

>spring.jpa.hibernate.ddl-auto=update
spring.datasource.url=//your url
spring.datasource.username=//your username
spring.datasource.password=//your password
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.servlet.multipart.max-file-size=8MB
spring.servlet.multipart.max-request-size=8MB
>server.error.whitelabel.enabled=false
 
####Also you need to implement filters in your "*ProjectName*" file.
