FROM gradle:8.7-jdk21

ARG HOST_UID=1000
ARG HOST_GID=1000

WORKDIR /workspace
COPY --chown=${HOST_UID}:${HOST_GID} . .
RUN chown -R ${HOST_UID}:${HOST_GID} /workspace /home/gradle/.gradle

CMD ["gradle", "--no-daemon", "--stacktrace", \
     "checkstyleMain", "checkstyleTest", "test", "jacocoTestReport"]
