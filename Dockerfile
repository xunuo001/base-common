FROM mysql

ENV MYSQL_ROOT_PASSWORD='123456'
COPY docker/mysql/init/init.sql /docker-entrypoint-initdb.d/
COPY docker/mysql/conf/my.cnf /etc
COPY docker/start.sh /start.sh
RUN chmod 777 /start.sh

RUN mkdir /usr/local/java
ADD docker/jdk/jdk-linux-x64.tar.gz /usr/local/java
RUN ln -s /usr/local/java/jdk1.8.0_131 /usr/local/java/jdk
ENV JAVA_HOME /usr/local/java/jdk
ENV CLASSPATH .:${JAVA_HOME}/lib
ENV PATH ${JAVA_HOME}/bin:$PATH

ARG JAR_FILE
RUN mkdir /usr/myservice
ADD target/${JAR_FILE}  /usr/myservice/myservice.jar

CMD ["/bin/sh","-c","/start.sh"]