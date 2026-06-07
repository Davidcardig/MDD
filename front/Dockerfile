FROM node:20-alpine AS build
WORKDIR /usr/local/app
COPY package.json package-lock.json ./
RUN npm install
COPY src/ ./src/
COPY angular.json tsconfig.json tsconfig.app.json ./
RUN npm run build

FROM nginx:alpine AS production
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /usr/local/app/dist/front /usr/share/nginx/html
RUN chown -R appuser:appgroup /usr/share/nginx/html
USER appuser
EXPOSE 80
