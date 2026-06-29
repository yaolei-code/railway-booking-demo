# ---- 阶段1：编译打包 ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# 先复制 pom.xml，利用 Docker 缓存加速（依赖不变时不用重新下载）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 再复制源码并打包（跳过测试，测试在开发阶段跑过即可）
COPY src src
RUN mvn package -DskipTests -B

# ---- 阶段2：运行 ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 从构建阶段拿 jar 包
COPY --from=builder /build/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]
