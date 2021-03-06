package com.clawhub.registrycenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The type Server application.
 */
@SpringBootApplication
public class ServerApplication {

    /**
     * The constant LOGGER.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerApplication.class);

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext confApp = null;
        try {
            confApp = SpringApplication.run(ServerApplication.class, args);
        } finally {
            close(confApp);
        }
    }

    /**
     * Close.
     *
     * @param confApp the conf app
     */
    private static void close(ConfigurableApplicationContext confApp) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                if (confApp != null) {
                    confApp.close();
                    LOGGER.error("spring boot application has been closed .");
                }
            }
        });
    }
}
