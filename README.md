# Salutem


This project is supposed to be a security system for the raspberry pi written in scala.

## installation
to run this you need sbt and scala


### to build
```
sbt assembly
```


## Running

```
java -jar ./target/scala-2.11/Salutem-assembly-1.0.jar
```


### env variables you need

During local testing on no raspberry pi devices I needed a pin controller
to talk to without trying to boot up the wiring pi library
This env controlls whether or not you use the fake controller or the real one
this defaults to true if not specified
```
FAKE_PIN_CONTROLLER=false 
```