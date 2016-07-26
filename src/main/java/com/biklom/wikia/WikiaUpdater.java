package com.biklom.wikia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 *
 * @author biklom
 */
@SpringBootApplication
public class WikiaUpdater implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(WikiaUpdater.class);
    public static void main(String args[]) {
        SpringApplication.run(WikiaUpdater.class);
    }

    @Override
    public void run(String... args) throws Exception {
    }
}
