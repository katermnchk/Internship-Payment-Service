FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

WORKDIR /app/target/extracted
RUN jar -xf ../*.jar

FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/extracted/BOOT-INF/lib ./lib
COPY --from=build /app/target/extracted/BOOT-INF/classes .
COPY --from=build /app/target/extracted/org ./org

EXPOSE 8083

ENTRYPOINT ["java", "-cp", ".:./lib/*", "com.innowise.internship.InnowiseInternshipPaymentServiceApplication"]
