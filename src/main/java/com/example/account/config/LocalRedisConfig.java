package com.example.account.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class LocalRedisConfig {
  @Value("${spring.redis.port}")
  private int redisPort;

  // getter 및 필요한 로직 추가
  private RedisServer redisServer;

  @PostConstruct
  public void startRedis(){
    redisServer = new RedisServer(redisPort);
    redisServer.start();
  }

  @PreDestroy
  public void stopRedis(){
    if(redisServer != null){
      redisServer.stop();
    }
  }
}