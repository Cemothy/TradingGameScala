FROM hseeberger/scala-sbt:graalvm-ce-21.3.0-java17_1.6.2_3.1.1

WORKDIR /Tradinggamescala

COPY . .

#Probably have to add more dependencies for the GUI but which ones?
RUN microdnf update && \
    microdnf install -y libxrender libxtst libxi || true 

ENV DISPLAY=host.docker.internal:0.0
 
CMD ["sbt", "runMain de.htwg.se.TradingGame.Main"]

#set DISPLAY=ipv4  141.70.104.14:0.0
#docker run -it --rm -e DISPLAY=%DISPLAY% --network="host" --name gui_container tradinggamescala<or id>