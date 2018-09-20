FROM java:8
RUN mkdir /microservice
WORKDIR /microservice
ADD /config-center-1.0.jar /microservice/
EXPOSE 8888
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/microservice/config-center-1.0.jar"]
