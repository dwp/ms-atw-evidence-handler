FROM gcr.io/distroless/java11@sha256:a83115dc78baf90e1ae41bf6eaa2bfe1e5b0f8afb91bd7330a488d413322fcb2
EXPOSE 9013

COPY target/ms-evidence-handler-*.jar /ms-evidence-handler.jar
ENTRYPOINT ["java", "-jar",  "/ms-evidence-handler.jar"]
