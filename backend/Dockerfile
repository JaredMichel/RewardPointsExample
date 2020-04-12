FROM adoptopenjdk:11-jre-hotspot
RUN mkdir /app
COPY ti-api2.jar /app/ti-api2.jar
WORKDIR /app
CMD ["java", "-jar", "ti-api2.jar"]