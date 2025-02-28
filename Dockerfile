FROM gcr.io/distroless/java17@sha256:38e4b51e5fbd44e5b3f8d77bcc8ae573f265174249dad7316aa3a9ce0ada0cfc

USER nonroot

COPY --from=pik94420.live.dynatrace.com/linux/oneagent-codemodules:java / /
ENV LD_PRELOAD /opt/dynatrace/oneagent/agent/lib64/liboneagentproc.so

EXPOSE 9013

COPY target/ms-evidence-handler-*.jar /ms-evidence-handler.jar


HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:9013/healthcheck || exit 1

ENTRYPOINT ["java", "-jar",  "/ms-evidence-handler.jar"]
