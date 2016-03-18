# Template for Spring Boot Project


## Start Test Environment

```
$ docker-compose start database # start database
$ psql.sh # connect to test db (user + password = postgres)
$ export DATABASE_IP=$(docker-machine ip <machine name>)
```

https://github.com/spotify/docker-maven-plugin
```
mvn clean package docker:build
``

## Start App
```
$ docker-compose up -d
```


## Profiles

- `production`
- `test`

### Start service with profile
```
java -jar -Dspring.profiles.active=production demo-0.0.1-SNAPSHOT.jar
```

## TEST NOTE

Make sure your Docker machine is running and has service url
`https://192.168.99.100:2376`. You can change the service url in the
`application.yaml` (docker.machine-service-url).
