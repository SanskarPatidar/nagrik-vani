# NOTE: This Dockerfile is designed to be placed in each module of root multi-module project.
# NOTE: We could go from generic to specific Dockerfile to further increase build time of image

# Docker stages work like this:
# Each stage starts with a clean filesystem from its base image
# Only files explicitly copied with COPY --from=previous-stage are transferred between stages

# This Dockerfile accepts the module directory name as an argument
# Mention args in docker-compose.yml file while building the environment
ARG MODULE_DIR

# STAGE 1: Dependency Resolver (uses only POM files) - decoupled this from builder stage
# This stage is optimized for caching by downloading dependencies before working on source code.
FROM eclipse-temurin:21-jdk-alpine AS dependencies
WORKDIR /app

# Copy only the files needed to resolve dependencies and create the full dependency tree
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

COPY api-gateway/pom.xml ./api-gateway/
COPY auth-service/pom.xml ./auth-service/
COPY citizen-service/pom.xml ./citizen-service/
COPY complaint-service/pom.xml ./complaint-service/
COPY config-server/pom.xml ./config-server/
COPY department-admin-service/pom.xml ./department-admin-service/
COPY department-staff-service/pom.xml ./department-staff-service/
COPY issue-service/pom.xml ./issue-service/
COPY service-registry/pom.xml ./service-registry/
# (If you add more modules, add a COPY line for their pom.xml here)

# settings.xml is present at the root directory, if not then bring it anyhow
COPY settings.xml /root/.m2/settings.xml
RUN ./mvnw dependency:go-offline --settings /root/.m2/settings.xml

# STAGE 2: Builder (compiles the application and creates the layered JAR rather than fat jar)
# Requires whole jdk for building the application, rather than just jre
FROM eclipse-temurin:21-jdk-alpine AS builder
# This brings the top level variable into the stage's scope
ARG MODULE_DIR
WORKDIR /app
# Copy the cached maven dependencies from the previous stage
COPY --from=dependencies /root/.m2 /root/.m2

# Copy from a previous stage would only be more efficient if these files were very large or if you were copying many files that would significantly impact build time.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

COPY api-gateway/pom.xml ./api-gateway/
COPY auth-service/pom.xml ./auth-service/
COPY citizen-service/pom.xml ./citizen-service/
COPY complaint-service/pom.xml ./complaint-service/
COPY config-server/pom.xml ./config-server/
COPY department-admin-service/pom.xml ./department-admin-service/
COPY department-staff-service/pom.xml ./department-staff-service/
COPY issue-service/pom.xml ./issue-service/
COPY service-registry/pom.xml ./service-registry/

COPY ${MODULE_DIR}/src ./${MODULE_DIR}/src
RUN ./mvnw package -DskipTests -pl ${MODULE_DIR} -am

# STAGE 3: Extractor (extracts the layers from the JAR)
FROM eclipse-temurin:21-jre-alpine AS extractor
ARG MODULE_DIR
# Just to keep things organized, create a directory for extraction
WORKDIR /extracted
# Copy the specific JAR from the builder stage
COPY --from=builder /app/${MODULE_DIR}/target/*.jar application.jar
# Extract the layers using Spring Boot's built-in support
RUN java -Djarmode=layertools -jar application.jar extract

# STAGE 4: Final Image Creation (assembles the lean, production image from the layers)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /application
# Copy the extracted layers from the extractor stage in the optimal order
COPY --from=extractor /extracted/dependencies/ ./
COPY --from=extractor /extracted/spring-boot-loader/ ./
COPY --from=extractor /extracted/snapshot-dependencies/ ./
COPY --from=extractor /extracted/application/ ./

# Container listens on port 8080
EXPOSE 8080
# Added .launch for spring boot version 3.2+, for lesser versions remove .launch
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]