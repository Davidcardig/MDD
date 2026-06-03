FROM node:20-alpine AS build
WORKDIR /usr/local/app
COPY ./ /usr/local/app/
RUN npm install
RUN npm run build

FROM nginx:latest AS production
COPY --from=build /usr/local/app/dist/front /usr/share/nginx/html
EXPOSE 80
