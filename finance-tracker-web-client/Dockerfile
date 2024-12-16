FROM node:18 AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .

RUN npm run build

FROM node:18 AS prod

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install --only=production

COPY --from=build /app/dist /app/dist

RUN npm install -g vite

EXPOSE 5173

CMD ["vite", "preview", "--port", "5173", "--host"]
