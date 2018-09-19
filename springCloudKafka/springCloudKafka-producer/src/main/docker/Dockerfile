FROM java:8
ADD /springCloudKafka-producer-1.0.jar //
EXPOSE 1001
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/springCloudKafka-producer-1.0.jar"]
