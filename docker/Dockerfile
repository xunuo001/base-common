FROM mysql

ENV MYSQL_ROOT_PASSWORD='123456'
COPY mysql/init/init.sql /docker-entrypoint-initdb.d/
COPY mysql/conf/my.cnf /etc
COPY start.sh /start.sh
RUN chmod 777 /start.sh



CMD ["/bin/sh","-c","/start.sh"]