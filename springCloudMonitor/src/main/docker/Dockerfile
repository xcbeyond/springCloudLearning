FROM java:8
RUN mkdir /microservice
WORKDIR /microservice
ADD /monitor-1.0.jar /microservice/
EXPOSE 8889
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/microservice/monitor-1.0.jar"]