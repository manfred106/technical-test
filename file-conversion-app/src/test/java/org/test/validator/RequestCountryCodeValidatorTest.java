package org.test.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.validation.Errors;
import org.springframework.validation.SimpleErrors;
import org.test.config.ApplicationConfig;
import org.test.interceptor.IpGeoLocationResponse;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RequestCountryCodeValidatorTest {

    @Spy
    private ApplicationConfig applicationConfig = ApplicationConfig.builder()
            .blockedCountryCodes(Set.of("CN", "ES", "US"))
            .build();

    @InjectMocks
    private RequestCountryCodeValidator requestCountryCodeValidator;

    @Test
    void whenClassIsNotSupported_thenReturnFalse() {
        assertThat(requestCountryCodeValidator.supports(String.class)).isFalse();
    }

    @Test
    void whenCountryCodeIsNotBlocked_thenNoError() {
        // when
        IpGeoLocationResponse response = getIpGeoLocationResponse("FR");
        Errors errors = new SimpleErrors(response);
        requestCountryCodeValidator.validate(response, errors);

        // then
        assertThat(errors.getAllErrors()).overridingErrorMessage("Errors should be empty").isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"CN", "ES", "US"})
    void whenCountryCodeIsBlocked_thenErrorIsNotEmpty(String countryCode) {
        // when
        IpGeoLocationResponse response = getIpGeoLocationResponse(countryCode);
        Errors errors = new SimpleErrors(response);
        requestCountryCodeValidator.validate(response, errors);

        // then
        assertThat(errors.getAllErrors()).overridingErrorMessage("Errors should not be empty").isNotEmpty();
        assertThat(errors.getAllErrors().get(0).getCode()).isEqualTo("countryCode");
    }

    private IpGeoLocationResponse getIpGeoLocationResponse(String countryCode) {
        IpGeoLocationResponse response = new IpGeoLocationResponse();
        response.setCountryCode(countryCode);
        return response;
    }

}