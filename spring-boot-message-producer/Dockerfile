FROM openjdk:11

# Add a volume pointing to /tmp
VOLUME /tmp

# Make port 8085 available to the world outside this container
EXPOSE 8090

# The application's jar file
ADD target/message-queue-producer.jar message-queue-producer.jar

# Add the json schema file to docker
ADD src/main/java/com/example/messagepublisher/controller/messageSchema.json messageSchema.json

ENTRYPOINT ["java", "-jar","message-queue-producer.jar"]