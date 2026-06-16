## Stage 1 : build with maven builder image with native capabilities
FROM observabilitystack/graalvm-maven-builder:21.0.1-ol9  AS builder

WORKDIR /build

COPY pom.xml .
COPY settings.xml .
COPY src src/
RUN mvn package -Pnative --quiet -s settings.xml

## Stage 2 : create the docker final image
FROM registry.access.redhat.com/ubi9-minimal:9.2

WORKDIR /app
COPY --from=builder /build/target/*-runner /app/application

# set up permissions for user `1001`
RUN chmod 775 /app /app/application \
  && chown -R 1001 /app \
  && chmod -R "g+rwX" /app \
  && chown -R 1001:root /app

EXPOSE 8080
USER 1001
CMD ["./application", "-Dquarkus.http.host=0.0.0.0"]
