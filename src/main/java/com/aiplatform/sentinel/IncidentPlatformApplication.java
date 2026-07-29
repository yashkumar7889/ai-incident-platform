package com.aiplatform.sentinel;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IncidentPlatformApplication {

	public static void main(String[] args) {

		// JVM setting the TimeZone to Asia/Calcutta which is causing issue, so using
		// below code to set the TimeZone to Asia/Kolkata
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));

		SpringApplication.run(IncidentPlatformApplication.class, args);
	}

}
