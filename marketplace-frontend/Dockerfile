# Stage 1: Build the Angular application
FROM node:20-alpine AS build
WORKDIR /app

# Install dependencies first (layer caching)
COPY package.json package-lock.json ./
RUN npm ci

# Copy source and build
COPY . .
RUN npm run build

# Stage 2: Serve with NGINX
FROM nginx:alpine
# Remove default NGINX index page
RUN rm -rf /usr/share/nginx/html/*

# Copy the NGINX config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy the compiled Angular app 
# Note: Angular 17+ outputs to dist/<project-name>/browser
COPY --from=build /app/dist/marketplace-frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]