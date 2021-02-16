FROM hseeberger/scala-sbt AS builder
WORKDIR /app
COPY . .
RUN sbt stage

FROM openjdk:8-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/universal/stage .
ENTRYPOINT ["bin/pnia"]