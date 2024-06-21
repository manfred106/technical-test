package org.test.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.springframework.validation.Errors;
import org.springframework.validation.SimpleErrors;
import org.test.config.ApplicationConfig;
import org.test.interceptor.IpGeoLocationResponse;

import java.util.Set;

@ExtendWith(MockitoExtension.class)
class RequestCountryCodeValidatorTest {

    @Mock
    private ApplicationConfig applicationConfig;

    @InjectMocks
    private RequestCountryCodeValidator requestCountryCodeValidator;

    @Test
    void whenClassIsNotSupported_thenReturnFalse() {
        assertThat(requestCountryCodeValidator.supports(String.class)).isFalse();
    }

    @Test
    void whenCountryCodeIsNotBlocked_thenNoError() {
        // given
        stubBlockedCountryCodes();

        // when
        IpGeoLocationResponse response = getIpGeoLocationResponse("FR");
        Errors errors = new SimpleErrors(response);
        requestCountryCodeValidator.validate(response, errors);

        // then
        assertThat(errors.getAllErrors()).overridingErrorMessage("Errors should be empty").isEmpty();
    }

    @Test
    void whenCountryCodeIsBlocked_thenErrorIsNotEmpty() {
        // given
        stubBlockedCountryCodes();

        // when
        IpGeoLocationResponse response = getIpGeoLocationResponse("CN");
        Errors errors = new SimpleErrors(response);
        requestCountryCodeValidator.validate(response, errors);

        // then
        assertThat(errors.getAllErrors()).overridingErrorMessage("Errors should not be empty").isNotEmpty();
        assertThat(errors.getAllErrors().get(0).getCode()).isEqualTo("countryCode");
    }


    private void stubBlockedCountryCodes() {
        when(applicationConfig.getBlockedCountryCodes()).thenReturn(Set.of("CN", "US"));
    }

    private IpGeoLocationResponse getIpGeoLocationResponse(String countryCode) {
        IpGeoLocationResponse response = new IpGeoLocationResponse();
        response.setCountryCode(countryCode);
        return response;
    }

}