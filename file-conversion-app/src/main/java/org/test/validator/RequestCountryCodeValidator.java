package org.test.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.test.config.ApplicationConfig;
import org.test.interceptor.IpGeoLocationResponse;

@Component
@EnableConfigurationProperties(ApplicationConfig.class)
public class RequestCountryCodeValidator implements Validator {

    @Autowired
    private ApplicationConfig applicationConfig;

    public boolean supports(Class clazz) {
        return IpGeoLocationResponse.class.equals(clazz);
    }

    public void validate(Object obj, Errors errors) {
        IpGeoLocationResponse response = (IpGeoLocationResponse) obj;
        if (applicationConfig.getBlockedCountryCodes().contains(response.getCountryCode())) {
            errors.reject("countryCode", String.format("%s is blocked from access", response.getCountryCode()));
        }
    }

}
