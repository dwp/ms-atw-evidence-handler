FROM gcr.io/distroless/java11@sha256:c72d801a69d7a4996ce4f9d9d6fb1473867a6f778b5f39be7a7a7d6457f655ab
EXPOSE 9013

COPY target/ms-evidence-handler-*.jar /ms-evidence-handler.jar
ENTRYPOINT ["java", "-jar",  "/ms-evidence-handler.jar"]
