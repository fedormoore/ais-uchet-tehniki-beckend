FROM amazoncorretto:11.0.9-alpine as corretto-jdk

# требуется, чтобы работал strip-debug
RUN apk add --no-cache binutils

# собираем маленький JRE-образ
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

# главный образ приложения
FROM alpine:latest
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# копируем JRE из базового образа
COPY --from=corretto-jdk /customjre $JAVA_HOME

LABEL maintainer="fedormoore@gmail.com"
ARG ARTIFACT_NAME
ARG IMAGE_VERSION
EXPOSE 5000
COPY target/${ARTIFACT_NAME}*.jar /app/app.jar
WORKDIR /app
ENTRYPOINT [ "java", "-jar", "/app/app.jar" ]