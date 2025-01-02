FROM node:18 AS build

ARG REACT_APP_API_URL
ARG REACT_APP_ENV

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .

ENV REACT_APP_API_URL=$REACT_APP_API_URL
ENV REACT_APP_ENV=$REACT_APP_ENV

RUN npm run build

FROM node:18 AS prod

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install --only=production

COPY --from=build /app/dist /app/dist

RUN npm install -g vite

EXPOSE 5173

ENV NODE_ENV=production
ENV API_URL=$REACT_APP_API_URL

CMD ["vite", "preview", "--port", "5173", "--host"]