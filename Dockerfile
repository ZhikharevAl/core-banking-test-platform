FROM gradle:8.7-jdk21

WORKDIR /workspace
COPY --chown=gradle:gradle . .

CMD ["gradle", "--no-daemon", "--stacktrace", \
     "checkstyleMain", "checkstyleTest", "test", "jacocoTestReport"]
