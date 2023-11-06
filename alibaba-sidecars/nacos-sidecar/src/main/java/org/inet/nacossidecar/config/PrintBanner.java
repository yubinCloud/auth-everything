package org.inet.nacossidecar.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class PrintBanner implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(PrintBanner.class);

    @Value("${spring.application.name}")
    private String applicationName;

    private static final String BANNER =
            "\n" + " _   _   ___   _____  _____  _____          _____  _____ ______  _____  _____   ___  ______ \n" +
                    "| \\ | | / _ \\ /  __ \\|  _  |/  ___|        /  ___||_   _||  _  \\|  ___|/  __ \\ / _ \\ | ___ \\\n" +
                    "|  \\| |/ /_\\ \\| /  \\/| | | |\\ `--.  ______ \\ `--.   | |  | | | || |__  | /  \\// /_\\ \\| |_/ /\n" +
                    "| . ` ||  _  || |    | | | | `--. \\|______| `--. \\  | |  | | | ||  __| | |    |  _  ||    / \n" +
                    "| |\\  || | | || \\__/\\\\ \\_/ //\\__/ /        /\\__/ / _| |_ | |/ / | |___ | \\__/\\| | | || |\\ \\ \n" +
                    "\\_| \\_/\\_| |_/ \\____/ \\___/ \\____/         \\____/  \\___/ |___/  \\____/  \\____/\\_| |_/\\_| \\_|";

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info("sidecar <" + applicationName + "> successfully started.");
        logger.info(BANNER);
    }
}
