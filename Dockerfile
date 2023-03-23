FROM java:8
VOLUME /tmp
ADD minio-0.0.1-SNAPSHOT.jar app.jar
ENV TZ=Asia/Shanghai
RUN sh -c 'touch /app.jar' && ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
ENV JAVA_OPTS=""
ENV SERVER_PORT=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar --server.port=$SERVER_PORT" ]