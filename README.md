## Spring Boot Sample – Tomcat JNDI

Author: [@wilkinsona](https://github.com/wilkinsona)

Spring Boot sample application that demonstrates the configuration and use of
JNDI with embedded Tomcat.

Tested with Spring Boot 1.2.7.RELEASE and 1.3.0.RELEASE
Tested with Java 1.7 and 1.8

To change Spring Boot version:
###### pom.xml
```xml
<parent>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-parent</artifactId>
  <version>{Your Spring Boot Version Here}</version>
  <relativePath/> <!-- lookup parent from repository -->
</parent>
```

To change Java version:
###### pom.xml
```xml
<properties>
  <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	<start-class>sample.tomcat.jndi.SampleTomcatJndiApplication</start-class>
	<java.version>{Your Java Version Here}</java.version>
<properties>
```
