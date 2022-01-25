FROM java:8

COPY start.sh /start.sh
RUN chmod 777 /start.sh
ARG JAR_FILE
RUN mkdir /usr/myservice
ADD target/${JAR_FILE}  /usr/myservice/myservice.jar
EXPOSE 8888
CMD ["/bin/sh","-c","/start.sh"]