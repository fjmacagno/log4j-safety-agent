package com.foursquare.log4jauditor;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;

public final class Log4jAuditor implements ClassFileTransformer {

    private static final List<String> SAFE_LOG4J_VERSIONS = Arrays.asList(".*log4j-core-2\\.15\\.0-rc2.*");
    private boolean aggressive;
    private boolean ignoreVersions;

    public Log4jAuditor(boolean aggressive, boolean ignoreVersions) {
        this.aggressive = aggressive;
        this.ignoreVersions = ignoreVersions;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        boolean aggressive = false;
        boolean ignoreVersions = false;

        if (agentArgs != null) {
            if (agentArgs.contains("aggressive=true")) {
                aggressive = true;
            }

            if (agentArgs.contains("ignoreVersions=true")) {
                ignoreVersions = true;
            }
        }

        inst.addTransformer(new Log4jAuditor(aggressive, ignoreVersions));
    }

    private boolean isLog4JVersionSafe(CodeSource path) {
        String file = path.getLocation().getFile();
        return SAFE_LOG4J_VERSIONS.stream().anyMatch(file::matches);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        String classShortName = className.substring(className.lastIndexOf('/') + 1);

        if (classShortName.equals("JndiManager")) {
            String location = protectionDomain.getCodeSource().getLocation().getFile();
            System.out.println("Found suspicious class " + className + " at location: " + location);

            if (!ignoreVersions && isLog4JVersionSafe(protectionDomain.getCodeSource())) {
                System.out.println("Log4J version looks safe, permitting its existence");
            } else {
                System.out.println("The JVM attempted to load a JNDI Class, and the version doesn't look safe! Killing JVM for safety. Class name: " + className);
                if (!aggressive) {
                    try {
                        return emptyClassBytes();
                    } catch (IOException e) {
                        // This is bad, since this is required to break things properly. Pass so we can try something more dramatic...
                    }
                }

                Runtime.getRuntime().halt(1); // You shall not pass
            }
        }

        return classfileBuffer;
    }

    private static byte[] emptyClassBytes() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InputStream is = Log4jAuditor.class.getResourceAsStream("/com/foursquare/log4jauditor/FriendlyNeighborhoodJNDIReplacement.class");
        if (is == null) {
            throw new IOException("Could not find FriendlyNeighborhoodJNDIReplacement.class");
        }

        int nRead;
        byte[] data = new byte[4];
        while (true) {
            if ((nRead = is.read(data, 0, data.length)) == -1) break;
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}
