package com.amazonas.backend;

import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.users.RegisterRequest;
import com.amazonas.common.utils.JsonUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}


	public static void generateRequest() {
		RegisterRequest registerRequest = new RegisterRequest("yuval@email.com", "yuval", "Password123");
		Request request = new Request("yuval", "auth", JsonUtils.serialize(registerRequest));
		System.out.println(request.toJson());
	}

	@EventListener
	public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
		generateRequest();
	}
}
