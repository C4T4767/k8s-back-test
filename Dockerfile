FROM gradle:8.5-jdk17 AS build
WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
RUN gradle build || return 0

COPY . .
RUN gradle clean bootJar

FROM eclipse-temurin:17-jdk
WORKDIR /app

ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
ENV NAVER_CLIENT_ID=${NAVER_CLIENT_ID}
ENV NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET}
ENV JWT_SECRET=${JWT_SECRET}

# 🔹 Config 파일 복사
COPY src/main/resources/application.yml ./config/application.yml

# 🔹 빌드된 jar 복사
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

# 🔹 config 경로 지정해서 실행
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:./config/application.yml"]
