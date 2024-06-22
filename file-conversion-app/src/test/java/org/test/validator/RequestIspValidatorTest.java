package org.test.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;
import org.springframework.validation.SimpleErrors;
import org.test.config.ApplicationConfig;
import org.test.interceptor.IpGeoLocationResponse;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RequestIspValidatorTest {

    @Spy
    private ApplicationConfig applicationConfig = ApplicationConfig.builder()
            .blockedIsps(Set.of("AWS", "GCP", "Azure"))
            .build();

    @InjectMocks
    private RequestIspValidator requestIspValidator;

    @Test
    void whenClassIsNotSupported_thenReturnFalse() {
        assertThat(requestIspValidator.supports(String.class)).isFalse();
    }

    @Test
    void whenIspIsNotBlocked_thenNoError() {
        // when
        IpGeoLocationResponse response = getIpGeoLocationResponse("valid-isp");
        Errors errors = new SimpleErrors(response);
        requestIspValidator.validate(response, errors);

        // then
        assertThat(errors.getAllErrors()).overridingErrorMessage("Errors should be empty").isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"AWS", "GCP", "Azure"})
    void whenIspIsBlocked_thenErrorIsNotEmpty(String isp) {
        // when
        IpGeoLocationResponse response = getIpGeoLocationResponse(isp);
        Errors errors = new SimpleErrors(response);
        requestIspValidator.validate(response, errors);

        // then
        assertThat(errors.getAllErrors()).overridingErrorMessage("Errors should not be empty").isNotEmpty();
        assertThat(errors.getAllErrors().get(0).getCode()).isEqualTo("isp");
    }

    private IpGeoLocationResponse getIpGeoLocationResponse(String isp) {
        IpGeoLocationResponse response = new IpGeoLocationResponse();
        response.setIsp(isp);
        return response;
    }

}