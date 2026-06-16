package com.help.desk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiHelpDeskApplication {

    private static final Logger logger = LoggerFactory.getLogger(AiHelpDeskApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(AiHelpDeskApplication.class, args);
        logger.info("AI Help Desk Application Started");
	}

}
