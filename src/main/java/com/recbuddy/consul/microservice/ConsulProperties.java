package com.recbuddy.consul.microservice;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties("consul")
@Data
public class ConsulProperties {

	private String prop = "default value";

	public String getProp() {
		return prop;
	}
	
	
}
