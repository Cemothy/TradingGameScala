FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1

WORKDIR /Tradinggamescala

COPY . .

RUN microdnf update && \
    microdnf install -y libxrender libxtst libxi || true

ENV DISPLAY=host.docker.internal:0.0

CMD ["sbt", "runMain", "Databaseask"]
