clear
mvn clean package
if [ $? -eq 0 ]; then
  clear
  java -cp target/game-1.0-SNAPSHOT.jar Main
fi