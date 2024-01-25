FROM sbtscala/scala-sbt:eclipse-temurin-focal-17.0.9_9_1.9.8_3.3.1

RUN apt-get update && apt-get install -y libxrender1 libxtst6 libxi6 libgl1-mesa-glx libgtk-3-0

WORKDIR /Tradinggamescala

ADD . /Tradinggamescala

CMD ["sbt", "runMain de.htwg.se.TradingGame.Main"]



