package org.test.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@ToString
@ConfigurationProperties("application")
public class ApplicationConfig {

    @Setter
    private Boolean geoLocationValidation;

    @Setter
    private String geoLocationUrl;

    private Set<String> blockedCountryCodes;

    private Set<String> blockedIsps;

}
