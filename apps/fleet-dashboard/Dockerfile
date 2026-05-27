FROM node:20-alpine AS build
WORKDIR /workspace

COPY package.json package-lock.json* nx.json tsconfig.base.json ./
COPY apps/fleet-dashboard apps/fleet-dashboard
COPY libs/shared-contracts libs/shared-contracts
COPY libs/shared-ui libs/shared-ui
RUN npm ci
RUN npx nx build fleet-dashboard --configuration=production

FROM nginx:1.27-alpine
COPY apps/fleet-dashboard/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/apps/fleet-dashboard/browser /usr/share/nginx/html
EXPOSE 80
