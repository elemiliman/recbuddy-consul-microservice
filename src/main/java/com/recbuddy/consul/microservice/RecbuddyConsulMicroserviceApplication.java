package com.recbuddy.consul.microservice;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
@EnableConfigurationProperties
@Slf4j
public class RecbuddyConsulMicroserviceApplication implements ApplicationListener<RemoteApplicationEvent > {

	final Logger logger = LoggerFactory.getLogger(RecbuddyConsulMicroserviceApplication.class);

	@Autowired
	private LoadBalancerClient loadBalancer;

	@Autowired
	private DiscoveryClient discoveryClient;

	@Autowired
	private Environment env;

	@Autowired(required = false)
	private RelaxedPropertyResolver resolver;

	@Value("${spring.application.name:consul}")
	private String appName;

	@PostConstruct
	public void init() {
		if (resolver == null) {
			resolver = new RelaxedPropertyResolver(env);
		}
	}

	@RequestMapping("/me")
	public ServiceInstance me() {
		return discoveryClient.getLocalServiceInstance();
	}

	@RequestMapping("/")
	public ServiceInstance lb() {
		return loadBalancer.choose(appName);
	}

	@RequestMapping("/myenv")
	public String env(@RequestParam("prop") String prop) {
		return resolver.getProperty(prop, "Not Found");
	}

	@RequestMapping("/prop")
	public String prop() {
		return sampleProperties().getProp();
	}

	@Bean
	public ConsulProperties sampleProperties() {
		return new ConsulProperties();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(RecbuddyConsulMicroserviceApplication.class, args);
	}

	@Override
	public void onApplicationEvent(RemoteApplicationEvent  event) {
		
		logger.info("Received event: {}", event);
		
	}
}
