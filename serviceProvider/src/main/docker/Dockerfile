FROM java:8
RUN mkdir /microservice
WORKDIR /microservice
ADD /service-provider-1.0.jar /microservice/
EXPOSE 8001
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/microservice/service-provider-1.0.jar"]
