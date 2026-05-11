mvn spring-boot:run -Dspring-boot.run.profiles=h2 > app.log 2>&1 &
echo $! > app.pid
