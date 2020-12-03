FROM java:8
WORKDIR /
ADD /build/libs/tiny-concurrent-server-all-1.0-SNAPSHOT.jar tiny-concurrent-server-all-1.0-SNAPSHOT.jar
EXPOSE 8080
CMD java -jar tiny-concurrent-server-all-1.0-SNAPSHOT.jar