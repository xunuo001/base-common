mkdir /mysql_data
mysqld --initialize-insecure
sleep 5
nohup mysqld &
sleep 5
mysql -uroot -Dmysql</docker-entrypoint-initdb.d/init.sql
java -jar /usr/myservice/myservice.jar