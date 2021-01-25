FROM bde2020/spark-submit:3.0.1-hadoop3.2

LABEL maintainer="Prashant.Gupta"

ENV SPARK_APPLICATION_JAR_NAME MovieDataAnalysis-1.0-SNAPSHOT-jar-with-dependencies.jar
ENV SPARK_APPLICATION_MAIN_CLASS com.foo.movie.main.MovieMainCls
ENV SPARK_APPLICATION_JAR_LOCATION "target/"

COPY template.sh /

RUN apk add --no-cache openjdk8 maven\
      && chmod +x /template.sh \
      && mkdir -p /app \
      && mkdir -p /usr/src/app

# Copy the POM-file first, for separate dependency resolving and downloading
ONBUILD COPY pom.xml /usr/src/app
ONBUILD RUN cd /usr/src/app \
      && mvn dependency:resolve
ONBUILD RUN cd /usr/src/app \
      && mvn verify

# Copy the source code and build the application
ONBUILD COPY . /usr/src/app
ONBUILD RUN cd /usr/src/app \
      && mvn clean package

CMD ["/bin/bash", "/template.sh"]