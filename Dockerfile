# 使用Node.js官方镜像作为基础镜像
FROM node:18-alpine as build

# 设置工作目录
WORKDIR /app

# 复制package.json和pnpm-lock.yaml
COPY package*.json pnpm-lock.yaml ./

# 全局安装pnpm
RUN npm install -g pnpm

# 安装依赖
RUN pnpm install

# 复制源代码
COPY . .

# 构建应用
RUN pnpm run build

# 使用nginx作为生产环境服务器
FROM nginx:alpine

# 复制构建后的文件到nginx
COPY --from=build /app/dist /usr/share/nginx/html

# 复制nginx配置文件
COPY nginx.conf /etc/nginx/nginx.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"] 