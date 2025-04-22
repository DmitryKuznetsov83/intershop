FROM amazoncorretto:21.0.2
# debug
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9080
COPY target/*.jar inter_shop_server.jar
ENTRYPOINT ["java","-jar","/inter_shop_server.jar"]