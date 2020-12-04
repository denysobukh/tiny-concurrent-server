FROM openjdk:8-jre-alpine
ENV PORT=8080
WORKDIR /
ADD tiny-concurrent-server-all-1.0-SNAPSHOT.jar tiny-concurrent-server-all-1.0-SNAPSHOT.jar
EXPOSE $PORT
CMD java -Dserver.port=$PORT -jar tiny-concurrent-server-all-1.0-SNAPSHOT.jar
RUN apk --update --no-cache add curl
HEALTHCHECK CMD curl -f http://localhost:$PORT/ || exit 1