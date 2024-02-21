FROM nginx:alpine

MAINTAINER Ignat Krushev <ignat.krushev@bul-si.bg>

RUN apk --no-cache add bash curl nodejs npm chromium

ENV CHROME_BIN=/usr/bin/chromium-browser
ARG ANGULAR_PROFILES_ACTIVE=$ANGULAR_PROFILES_ACTIVE
#ARG NGINX_CONFIG_FILE=nginx-$ANGULAR_PROFILES_ACTIVE.conf

RUN set -x \
    && addgroup bfsa \
    && adduser -S -G bfsa -h /home/bfsa -s /bin/false bfsa \
    && mkdir -p /home/bfsa/logs \
    && chown -R bfsa:bfsa /home/bfsa

WORKDIR /home/bfsa
ENV PATH /home/bfsa:$PATH

ADD src /home/bfsa/src
ADD  *.js *.json /home/bfsa/
ADD nginx-custom.conf /etc/nginx/conf.d/default.conf

RUN ["npm", "config", "ls"]
RUN ["npm", "install"]
#RUN ["npm", "run", "eslint"]
#RUN ["npm", "run", "sonar"]
RUN ["npm", "test", "--no-watch --no-sandbox --code-coverage"]
RUN npx ng run bfsa-fe:build:$ANGULAR_PROFILES_ACTIVE

RUN ["rm", "-rf", "/usr/share/nginx/html/bfsa"]
RUN ["cp", "-ar", "/home/bfsa/dist/bfsa-fe", "/usr/share/nginx/html/bfsa"]

EXPOSE 80 443

#ENTRYPOINT nginx -g 'daemon off;'
#CMD ["nginx", "-g", "'daemon off;'"]
#CMD ["ls", "-la", "/home/bfsa"]
