# Usa una imagen base de Node.js
FROM node:18 AS build

# Define el directorio de trabajo
WORKDIR /app

# Copia el package.json y package-lock.json
COPY package*.json ./

# Instala las dependencias
RUN npm install

# Copia el resto del código de la aplicación
COPY . .

ARG REACT_APP_BASE_PATH

# Construye la aplicación
RUN npm run build

# Usa una imagen ligera para servir la aplicación
FROM nginx:alpine

# Copia los archivos construidos en el directorio adecuado de nginx
COPY --from=0 /app/build /usr/share/nginx/html

# Expone el puerto por defecto de nginx
EXPOSE 80

# Comando para iniciar nginx
CMD ["nginx", "-g", "daemon off;"]
