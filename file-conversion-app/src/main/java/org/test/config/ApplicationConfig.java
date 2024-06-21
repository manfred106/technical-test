package org.test.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Data
@ConfigurationProperties("application")
public class ApplicationConfig {

    private Set<String> blockedCountryCodes;

    private Set<String> blockedIsps;

}
