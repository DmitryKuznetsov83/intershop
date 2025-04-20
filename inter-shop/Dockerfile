FROM amazoncorretto:21.0.2
# debug
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
COPY target/*.jar intershop_server.jar
ENTRYPOINT ["java","-jar","/intershop_server.jar"]