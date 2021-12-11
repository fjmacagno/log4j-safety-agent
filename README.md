# Log4J Safety Agent
Quick and directy java agent which prevents JNDI from being instantiated, preventing the recent log4j vulnerability from being taken advantage of. See https://www.cyberkendra.com/2021/12/worst-log4j-rce-zeroday-dropped-on.html.

## Build
```bash
mvn package
```

## Usage
```bash
java -javaagent:./target/log4j-safety-agent-1.0-SNAPSHOT.jar -jar log4shell-vulnerable-app-0.0.1-SNAPSHOT.jar -i 127.0.0.1 -p 8888
```

See https://github.com/christophetd/log4shell-vulnerable-app for example vulnerable app.

### Arguments
Arguments can be passed via
```bash
-javaagent:./target/log4j-safety-agent-1.0-SNAPSHOT.jar=<arg>=<value>,<arg2>=<value2>,...
```

Optional Arguments:
`aggressive`: Always use `Runtime.halt()` instead of just emptying the JNDI class. Values: `true` or `false`
`ignoreVersions`: Don't check log4j version against list of safe versions. Values: `true` or `false`
