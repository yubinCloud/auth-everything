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
            """
 \n _   _               _____    ____     _____        _____   _____   _____    ______    _____              _____ \s
| \\ | |     /\\      / ____|  / __ \\   / ____|      / ____| |_   _| |  __ \\  |  ____|  / ____|     /\\     |  __ \\\s
|  \\| |    /  \\    | |      | |  | | | (___       | (___     | |   | |  | | | |__    | |         /  \\    | |__) |
| . ` |   / /\\ \\   | |      | |  | |  \\___ \\       \\___ \\    | |   | |  | | |  __|   | |        / /\\ \\   |  _  /\s
| |\\  |  / ____ \\  | |____  | |__| |  ____) |      ____) |  _| |_  | |__| | | |____  | |____   / ____ \\  | | \\ \\\s
|_| \\_| /_/    \\_\\  \\_____|  \\____/  |_____/      |_____/  |_____| |_____/  |______|  \\_____| /_/    \\_\\ |_|  \\_\\\
             """;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logger.info(BANNER + "\n" + "                                                            " + "sidecar <" + applicationName + "> successfully started.");
    }
}
