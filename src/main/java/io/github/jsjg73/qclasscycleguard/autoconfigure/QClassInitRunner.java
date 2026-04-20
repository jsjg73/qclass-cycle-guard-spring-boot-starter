package io.github.jsjg73.qclasscycleguard.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class QClassInitRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QClassInitRunner.class);
    private static final String RESOURCE_PATH = "META-INF/cyclic-qclasses.txt";

    @Override
    public void run(String... args) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(RESOURCE_PATH);
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            List<String> loaded = new ArrayList<>();
            long totalStart = System.currentTimeMillis();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) continue;
                    Class.forName(line, true, classLoader);
                    loaded.add(line);
                }
            }
            log.info("Loaded {} Q-classes from {} ({}ms): {}",
                    loaded.size(), extractSourceName(url), System.currentTimeMillis() - totalStart, loaded);
        }
    }

    private String extractSourceName(URL url) {
        String path = url.toString();
        int bang = path.indexOf('!');
        String filePart = bang >= 0 ? path.substring(0, bang) : path;
        int slash = filePart.lastIndexOf('/');
        return slash >= 0 ? filePart.substring(slash + 1) : filePart;
    }
}
