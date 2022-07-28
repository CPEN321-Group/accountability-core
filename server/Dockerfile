# syntax=docker/dockerfile:1
FROM node:12-alpine 
COPY . .
RUN npm install
CMD ["node", "src/index.js"]
EXPOSE 8000