# tiny-concurrent-server

simple java based server responding to GET / with 'Hello world' at high rate 

to build and run docker image:

```
./gradlew clean docker 
docker run -d -t -i -P --name tiny-concurrent-server-container tiny-concurrent-server:1.0 
docker coniner ls -a
```
