FROM java:8
ENV PORT=8080
WORKDIR /
ADD /build/libs/tiny-concurrent-server-all-1.0-SNAPSHOT.jar tiny-concurrent-server-all-1.0-SNAPSHOT.jar
EXPOSE $PORT
CMD java -Dserver.port=$PORT -jar tiny-concurrent-server-all-1.0-SNAPSHOT.jar