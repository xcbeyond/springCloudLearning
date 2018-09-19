FROM java:8
ADD /springCloudKakfa-consumer-1.0.jar //
EXPOSE 1002
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "springCloudKakfa-consumer-1.0.jar"]
