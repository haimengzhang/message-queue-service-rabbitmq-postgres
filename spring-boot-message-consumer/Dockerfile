FROM openjdk:11

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8091 available to the world outside this container
EXPOSE 8091

# The application's jar file
ADD target/message-queue-consumer.jar message-queue-consumer.jar

ENTRYPOINT ["java", "-jar","message-queue-consumer.jar"]