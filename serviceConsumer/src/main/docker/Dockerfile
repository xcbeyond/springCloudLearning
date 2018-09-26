FROM java:8
RUN mkdir /microservice
WORKDIR /microservice
ADD /service-consumer-1.0.jar /microservice/
EXPOSE 8002
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/microservice/service-consumer-1.0.jar"]
