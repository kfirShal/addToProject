package com.amazonas;

import com.amazonas.business.stores.Store;
import com.amazonas.utils.IdGenerator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.UUID;

@SpringBootApplication
public class Application{

    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
