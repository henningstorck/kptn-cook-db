package com.henningstorck.kptncookdb;

import org.springframework.boot.SpringApplication;

public class TestKptnCookDbApplication {

	public static void main(String[] args) {
		SpringApplication.from(KptnCookDbApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
