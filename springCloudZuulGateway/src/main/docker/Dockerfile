FROM java:8
RUN mkdir /microservice
WORKDIR /microservice
ADD /gateway-1.0.jar /microservice/
EXPOSE 8111
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/microservice/gateway-1.0.jar"]
