package com.zipline.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfig {
	@Value("${spring.data.redis.port}")
	private int port;

	@Value("${spring.data.redis.host}")
	private String host;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(host, port);
		return lettuceConnectionFactory;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate() {
		StringRedisTemplate redisTemplate = new StringRedisTemplate();
		redisTemplate.setConnectionFactory(redisConnectionFactory());
		return redisTemplate;
	}
}
