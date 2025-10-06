# Stage 1: Build Angular app
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install --silent
COPY . .
RUN npm run build

# Stage 2: static export (optional base)
# We don't use Nginx here anymore
FROM alpine:3.19 AS export
WORKDIR /dist
COPY --from=build /app/dist/ .
