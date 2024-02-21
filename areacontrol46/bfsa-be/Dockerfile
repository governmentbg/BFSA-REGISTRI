FROM alpine:edge

MAINTAINER Ignat Krushev <ignat.krushev@bul-si.bg>

RUN apk --no-cache add bash openjdk17 curl ttf-dejavu

RUN set -x \
    && addgroup bfsa \
    && adduser -S -G bfsa -h /home/bfsa -s /bin/false bfsa \
    && mkdir -p /home/bfsa/logs \
    && mkdir -p /home/bfsa/storage \
    && chown -R bfsa:bfsa /home/bfsa

WORKDIR /home/bfsa
ENV PATH /home/bfsa:$PATH

ADD bfsa-core /home/bfsa/bfsa-core
ADD bfsa-resource /home/bfsa/bfsa-resource
ADD pom.xml /home/bfsa/pom.xml
ADD .mvn /home/bfsa/.mvn
ADD mvnw /home/bfsa/mvnw

# The actions below are in Jenkinsfile
#RUN ["mvnw", "clean"]
#RUN ["mvnw", "test"]
#RUN ["mvnw", "package", "-DskipTests"]

EXPOSE 8080

ENTRYPOINT ["/usr/bin/java"]
CMD ["-jar", "-ea", "bfsa-resource/target/bfsa-be.jar"]

#ENV PROFILES=postgres,logging-production
#CMD ["-jar", "-ea", "-Dspring.profiles.active=${PROFILES}", "bfsa-resource/target/bfsa-be.jar"]

#CMD ["ls", "-la", "/home/bfsa"]
