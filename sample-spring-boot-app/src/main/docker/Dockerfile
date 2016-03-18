FROM java:8

EXPOSE 8080

CMD java -Xms128m -Xmx512M -jar -Dspring.profiles.active=production /*.jar

COPY *.jar /