FROM gcr.io/distroless/java11@sha256:a4dcd554d29a3977a57eba4e8305867f6a7f231261202e4fc93359642ef73807
EXPOSE 9013

COPY target/ms-evidence-handler-*.jar /ms-evidence-handler.jar
ENTRYPOINT ["java", "-jar",  "/ms-evidence-handler.jar"]
