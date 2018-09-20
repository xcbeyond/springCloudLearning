FROM java:8
RUN mkdir /microservice
WORKDIR /microservice
ADD /register-center-1.0.jar /microservice/
EXPOSE 8761
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/microservice/register-center-1.0.jar"]
