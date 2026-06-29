# ---- 阶段1：构建前端 ----
FROM node:20-alpine AS builder
WORKDIR /build

# 先复制包文件，利用 Docker 缓存
COPY package.json package-lock.json ./
RUN npm ci

# 复制源码并构建
COPY . .
RUN npm run build

# ---- 阶段2：Nginx 托管 ----
FROM nginx:alpine

# 把构建产物复制到 nginx 的静态文件目录
COPY --from=builder /build/dist /usr/share/nginx/html

# 替换 nginx 默认配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
