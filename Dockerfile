FROM gcr.io/distroless/java17@sha256:d0ca593abaf2415c6828cad12c5cc8757d1ca7f50d544233ef442f85f9a9fae1

USER nonroot

COPY --from=pik94420.live.dynatrace.com/linux/oneagent-codemodules:java / /
ENV LD_PRELOAD /opt/dynatrace/oneagent/agent/lib64/liboneagentproc.so

EXPOSE 9013

COPY target/ms-evidence-handler-*.jar /ms-evidence-handler.jar


HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:9013/healthcheck || exit 1

ENTRYPOINT ["java", "-jar",  "/ms-evidence-handler.jar"]
