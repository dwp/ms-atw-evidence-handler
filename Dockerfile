FROM gcr.io/distroless/java11@sha256:28f6bb7beac6ee3e2e20a5baf6e692c11744987afd36a1205fcb1378a82715ba
EXPOSE 9013

COPY target/ms-evidence-handler-*.jar /ms-evidence-handler.jar
ENTRYPOINT ["java", "-jar",  "/ms-evidence-handler.jar"]
