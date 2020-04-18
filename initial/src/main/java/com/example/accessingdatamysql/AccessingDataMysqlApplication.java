package com.example.accessingdatamysql;

import com.example.accessingdatamysql.dao.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class AccessingDataMysqlApplication {

	public static void main(String[] args) {

		SpringApplication.run(AccessingDataMysqlApplication.class, args);
	}

}