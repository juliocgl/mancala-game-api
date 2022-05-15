package com.example.mancala.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "custom")
@Getter
public class GameConfig {
    @Value("${littlePitsPerPlayer:6}")
    private Integer littlePitsPerPlayer;
    @Value("${initialStonesPerPit:6}")
    private Integer initialStonesPerPit;
}
