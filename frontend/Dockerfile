FROM node:16.17-alpine3.16

WORKDIR /usr/src/app/citytrees-frontend

COPY package*.json ./

RUN npm install

EXPOSE 3000

CMD ["npm", "start"]
