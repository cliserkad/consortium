# Consortium
A turn based game where players control a consortium of retail corporations to compete for market share.

## Play
Install Java 21 or later:  
https://learn.microsoft.com/en-us/java/openjdk/download#openjdk-2103-lts--see-previous-releases  
Download Latest Jar:  
https://github.com/cliserkad/consortium/releases/latest/download/consortium.jar  
Run the jar from a terminal:
```
java -jar consortium.jar 
```
Optionally, you can provide the ip and port as command line arguments:
```
java -jar consortium.jar <ip> <port>
```

## Build
Build from source:
```
git clone https://github.com/cliserkad/consortium
cd consortium
mvn clean package
cd target
java -jar consortium-0.0.1-jar-with-dependencies.jar <ip> <port>
```
