# syntax=docker/dockerfile:1

# O frontend é exportado como arquivos estáticos (next.config.ts: output "export").
# Em produção quem os serve é o jar do Spring Boot; aqui, no docker-compose, um
# nginx faz o papel para o ambiente local ficar parecido com o de produção.

FROM node:24-alpine AS deps
WORKDIR /app
COPY package.json package-lock.json* ./
RUN npm ci

FROM node:24-alpine AS build
WORKDIR /app
COPY --from=deps /app/node_modules ./node_modules
COPY . .

ARG NEXT_PUBLIC_API_URL
ARG NEXT_PUBLIC_VIDEO_URL
ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL \
    NEXT_PUBLIC_VIDEO_URL=$NEXT_PUBLIC_VIDEO_URL \
    NEXT_TELEMETRY_DISABLED=1

RUN npm run build

FROM nginx:1.29-alpine AS runtime

# O export gera um arquivo por rota (obrigado.html). O $uri.html no try_files é
# o que liga /obrigado ao arquivo, como o PaginasEstaticasController faz no jar.
RUN printf '%s\n' \
    'server {' \
    '  listen 3000;' \
    '  root /usr/share/nginx/html;' \
    '  location / {' \
    '    try_files $uri $uri.html $uri/ =404;' \
    '  }' \
    '}' > /etc/nginx/conf.d/default.conf

COPY --from=build /app/out /usr/share/nginx/html

EXPOSE 3000

HEALTHCHECK --interval=15s --timeout=5s --start-period=10s --retries=5 \
  CMD wget -q -O /dev/null http://127.0.0.1:3000/ || exit 1
