# Template for Spring Boot Project


```
$ docker-compose start database # start database
$ psql.sh # connect to test db (user + password = postgres)
$ export DATABASE_IP=$(docker-machine ip <machine name>)
```

https://github.com/spotify/docker-maven-plugin
mvn clean package docker:build

## start app
```
$ docker-compose up -d
```