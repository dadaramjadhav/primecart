FROM eclipse-temurin:17-jre

WORKDIR /app

RUN groupadd --system primecart \
    && useradd --system \
       --gid primecart \
       --no-create-home \
       --shell /usr/sbin/nologin \
       primecart

COPY --chown=primecart:primecart target/*.jar app.jar

USER primecart

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "/app/app.jar"]