# PTChecker

This folder contains a class which allows you to check when a Packet Tracer session is up and able to answer IPC requests.

It can also be used to measure the average response time of the instance for a predefine request.

## Compilation

This project is [mavenizied](https://maven.apache.org/).
Note that this class depends on the ptipc library.
I cannot provide a version of it as its intellectual property belongs to Cisco.

To prepare a _jar_, simply run:

```
mvn assembly:assembly
```

Afterwards, in the _target_ directory you will find two jars: _JPTChecker.jar_ and _JPTChecker-jar-with-dependencies.jar_.
I recommend you to use the second one as it might be more handy to run it in other computers (no need to install maven and move _ptipc_ there).

## Usage

```
java -jar JPTChecker-jar-with-dependencies.jar [hostname] [port]
```
