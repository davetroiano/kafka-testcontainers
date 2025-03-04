# Kafka TestContainers Smoke Test

This contains a smoke test of Kafka TestContainers compatibility on Kafka 3.8-4.0

## Prereqs

- Java 17

## Repro

This runs a parametrized test that simple tests whether `KafkaContainer` can be started and stopped using the following Docker images:

- `apache/kafka:3.8.0`
- `apache/kafka-native:3.8.0`
- `apache/kafka:3.9.0`
- `apache/kafka-native:3.9.0`
- `apache/kafka:4.0.0-rc0`
- `apache/kafka-native:4.0.0-rc0`

```shell
git clone git@github.com:davetroiano/kafka-testcontainers.git
cd kafka-testcontainers
./gradlew test
```

These six tests should pass since this is basic [`KafkaContainer`](https://java.testcontainers.org/modules/kafka/) usage.

Observations:

* The two `3.8.0` tests pass
* The other four tests (`apache/kafka:3.9.0`, `apache/kafka-native:3.9.0`, `apache/kafka:4.0.0-rc0`, `apache/kafka-native:4.0.0-rc0`) fail
* The `3.9.0` tests fail due to:
```noformat
    ===> Configuring ...
    Running in KRaft mode...
    ===> Launching ...
    log4j:ERROR Could not read configuration file from URL [file:/opt/kafka/config/tools-log4j.properties].
java.io.FileNotFoundException: /opt/kafka/config/tools-log4j.properties (No such file or directory)
   at java.base@21.0.2/java.io.FileInputStream.open0(Native Method)
   at java.base@21.0.2/java.io.FileInputStream.open(FileInputStream.java:213 undefined)
   at java.base@21.0.2/java.io.FileInputStream.<init>(FileInputStream.java:152 undefined)
   at java.base@21.0.2/java.io.FileInputStream.<init>(FileInputStream.java:106 undefined)
   at java.base@21.0.2/sun.net.www.protocol.file.FileURLConnection.connect(FileURLConnection.java:84 undefined)
   at java.base@21.0.2/sun.net.www.protocol.file.FileURLConnection.getInputStream(FileURLConnection.java:180 undefined)
   at org.apache.log4j.PropertyConfigurator.doConfigure(PropertyConfigurator.java:532 undefined)
   at org.apache.log4j.helpers.OptionConverter.selectAndConfigure(OptionConverter.java:485 undefined)
   at org.apache.log4j.LogManager.<clinit>(LogManager.java:115 undefined)
   at org.slf4j.impl.Reload4jLoggerFactory.<init>(Reload4jLoggerFactory.java:67 undefined)
   at org.slf4j.impl.StaticLoggerBinder.<init>(StaticLoggerBinder.java:72 undefined)
   at org.slf4j.impl.StaticLoggerBinder.<clinit>(StaticLoggerBinder.java:45 undefined)
   at org.slf4j.LoggerFactory.bind(LoggerFactory.java:150 undefined)
   at org.slf4j.LoggerFactory.performInitialization(LoggerFactory.java:124 undefined)
   at org.slf4j.LoggerFactory.getILoggerFactory(LoggerFactory.java:417 undefined)
   at org.slf4j.LoggerFactory.getLogger(LoggerFactory.java:362 undefined)
   at com.typesafe.scalalogging.Logger$.apply(Logger.scala:31 undefined)
   at kafka.utils.Log4jControllerRegistration$.<clinit>(Logging.scala:25 undefined)
   at kafka.docker.KafkaDockerWrapper$.<clinit>(KafkaDockerWrapper.scala:29 undefined)
   at kafka.docker.KafkaDockerWrapper.main(KafkaDockerWrapper.scala undefined)
   at java.base@21.0.2/java.lang.invoke.LambdaForm$DMH/sa346b79c.invokeStaticInit(LambdaForm$DMH)
log4j:ERROR Ignoring configuration file [file:/opt/kafka/config/tools-log4j.properties].
log4j:WARN No appenders could be found for logger (kafka.utils.Log4jControllerRegistration$). log4j:WARN Please initialize the log4j system properly.
log4j:WARN See http://logging.apache.org/log4j/1.2/faq.html#noconfig for more info.
Exception in thread "main" java.lang.IllegalArgumentException: requirement failed:
        advertised.listeners cannot use the nonroutable meta-address 0.0.0.0. Use a routable IP address.
   at scala.Predef$.require(Predef.scala:337 undefined)
   at kafka.server.KafkaConfig.validateValues(KafkaConfig.scala:1022 undefined)
   at kafka.server.KafkaConfig.<init>(KafkaConfig.scala:852 undefined)
   at kafka.server.KafkaConfig.<init>(KafkaConfig.scala:184 undefined)
   at kafka.tools.StorageTool$.$anonfun$execute$1(StorageTool.scala:79 undefined)
   at scala.Option.flatMap(Option.scala:283 undefined)
   at kafka.tools.StorageTool$.execute(StorageTool.scala:79 undefined)
   at kafka.tools.StorageTool$.main(StorageTool.scala:46 undefined)
   at kafka.docker.KafkaDockerWrapper$.main(KafkaDockerWrapper.scala:48 undefined)
   at kafka.docker.KafkaDockerWrapper.main(KafkaDockerWrapper.scala undefined)
   at java.base@21.0.2/java.lang.invoke.LambdaForm$DMH/sa346b79c.invokeStaticInit(LambdaForm$DMH undefined)
Wrap text
```
* The `4.0.0-rc0` tests fail due to what appears to be the same root cause:
```noformat
Exception in thread "main" java.lang.IllegalArgumentException: requirement failed:
        advertised.listeners cannot use the nonroutable meta-address 0.0.0.0. Use a routable IP address.
   at scala.Predef$.require(Predef.scala:337 undefined)
   at kafka.server.KafkaConfig.validateValues(KafkaConfig.scala:714 undefined)
   at kafka.server.KafkaConfig.<init>(KafkaConfig.scala:598 undefined)
   at kafka.server.KafkaConfig.<init>(KafkaConfig.scala:158 undefined)
   at kafka.tools.StorageTool$.$anonfun$execute$1(StorageTool.scala:79 undefined)
   at scala.Option.flatMap(Option.scala:283 undefined)
   at kafka.tools.StorageTool$.execute(StorageTool.scala:79 undefined)
   at kafka.tools.StorageTool$.main(StorageTool.scala:46 undefined)
   at kafka.docker.KafkaDockerWrapper$.main(KafkaDockerWrapper.scala:57 undefined)
   at kafka.docker.KafkaDockerWrapper.main(KafkaDockerWrapper.scala undefined)

Formatted by https://st.elmah.io
```
* Attempted to work around by explcitly adding a listener via [`withListener()`](https://www.javadoc.io/static/org.testcontainers/kafka/1.20.4/index.html?org/testcontainers/containers/KafkaContainer.html) but the error persists, maybe because it only registers _additional_ listeners rather than overwriting.
