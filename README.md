# Authorization service

## What is this?

This is a simple service for authorizing users using OAuth2 services

## How can I run?

1. Change the application configuration along the path src.main.kotlin.resources, namely the ssl configuration.
   For example, you can create a certificate using the command 
``` keytool -keystore keystore.jks -alias sampleAlias -genkeypair -keyalg RSA -keysize 4096 -validity 3 -dname 'CN=localhost, OU=ktor, O=ktor, L=Unspecified, ST=Unspecified, C=US' -ext 'SAN:c=DNS:localhost,IP:127.0.0.1' ``` and this password ``` foobar```
2. You need to add a pair of **CLIENT_ID** and **CLIENT_SECRET** for all registered OAuth services
3. Start `./gradlew :run`

## Запуск в docker
1. Собрать jar `./gradlew :buildFatJar`
2. Пересобрать образ `docker compose build`
3. Заполнить файл .env следующим:
   <code>
   YANDEX_CLIENT_ID=
   YANDEX_CLIENT_SECRET=
   GOOGLE_CLIENT_ID=
   GOOGLE_CLIENT_SECRET=
   </code>
3. Запустить `docker compose up`