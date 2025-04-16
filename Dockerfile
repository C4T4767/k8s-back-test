# 🔧 [1단계] 빌드 스테이지
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# 의존성 먼저 복사해서 캐싱
COPY build.gradle settings.gradle ./ 
COPY gradle ./gradle
RUN gradle build || return 0

# 전체 프로젝트 복사하고 빌드
COPY . .
RUN gradle clean bootJar

# 🏃 [2단계] 실행 스테이지 (최종 이미지)
FROM eclipse-temurin:17-jdk
WORKDIR /app

# 환경 변수 설정 (Jenkins 환경 변수들을 여기에 추가)
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV KAKAO_CLIENT_ID=${KAKAO_CLIENT_ID}
ENV GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
ENV GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
ENV NAVER_CLIENT_ID=${NAVER_CLIENT_ID}
ENV NAVER_CLIENT_SECRET=${NAVER_CLIENT_SECRET}
ENV JWT_SECRET=${JWT_SECRET}

# 빌드 결과물 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 오픈
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
