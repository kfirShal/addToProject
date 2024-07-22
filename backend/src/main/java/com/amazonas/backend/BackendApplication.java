package com.amazonas.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	public static String getFolderPath(){
		String folderPath = BackendApplication.class.getResource("BackendApplication.class").getPath();
		folderPath = folderPath.replace("%20"," "); //fix space character
		int index = folderPath.indexOf(".jar");
		if(index == -1){
			index = folderPath.indexOf("classes");
			folderPath = folderPath.substring(0, index);
		} else {
			folderPath = folderPath.substring(0, index);
			folderPath = folderPath.substring(0, folderPath.lastIndexOf("/")+1);
		}
		folderPath = folderPath.substring(folderPath.indexOf("C"));
		return folderPath;
	}
}
