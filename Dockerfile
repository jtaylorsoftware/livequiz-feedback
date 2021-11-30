# syntax=docker/dockerfile:1

FROM amazoncorretto:17.0.1 as base

WORKDIR /workspace/app
COPY gradlew .
COPY gradle gradle
COPY settings.gradle.kts .
COPY build.gradle.kts .
RUN ./gradlew dependencies
COPY src src
CMD ["./gradlew", "bootRun"]

FROM base as test
CMD ["./gradlew", "test"]

FROM base as development
CMD ["./gradlew", "bootRun", "-Pdebug", "-Parguments='--spring.profiles.active=dev'"]

FROM base as build
RUN ./gradlew clean bootJar
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM amazoncorretto:17.0.1 as production
USER 1000

VOLUME /tmp
ARG DEPENDENCY=/workspace/app/build/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.jtaylorsoftware.livequiz.api.quiz.QuizApplication", "--spring-profiles.active=prod"]
