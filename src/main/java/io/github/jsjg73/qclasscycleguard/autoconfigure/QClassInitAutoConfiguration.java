package io.github.jsjg73.qclasscycleguard.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
public class QClassInitAutoConfiguration {

    @Bean
    public CommandLineRunner qClassInitRunner() {
        return new QClassInitRunner();
    }
}
