package org.test.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.client.RestTemplate;
import org.test.config.ApplicationConfig;
import org.test.entity.RequestLog;
import org.test.exception.InvalidCountryCodeException;
import org.test.exception.InvalidIspException;
import org.test.validator.RequestCountryCodeValidator;
import org.test.validator.RequestIspValidator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.test.interceptor.RequestAttributeConstant.ATTR_REQUEST_LOG;

@ExtendWith(MockitoExtension.class)
class IpGeoLocationInterceptorTest {

    private static final IpGeoLocationResponse DEFUALT_RESPONSE = IpGeoLocationResponse.builder()
            .countryCode("US")
            .isp("some-isp")
            .build();

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private RequestCountryCodeValidator requestCountryCodeValidator;

    @Mock
    private RequestIspValidator requestIspValidator;

    @Spy
    private ApplicationConfig applicationConfig = ApplicationConfig.builder()
            .geoLocationValidation(true)
            .geoLocationUrl("some-url/")
            .build();

    @Spy
    private RequestLog requestLog = new RequestLog();

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private IpGeoLocationInterceptor interceptor;

    @Captor
    private ArgumentCaptor<Errors> errorsArgumentCaptor;

    @BeforeEach
    void setup() {
        ResponseEntity<IpGeoLocationResponse> responseEntity = ResponseEntity.ok(DEFUALT_RESPONSE);
        when(restTemplate.getForEntity(any(String.class), eq(IpGeoLocationResponse.class))).thenReturn(responseEntity);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getAttribute(ATTR_REQUEST_LOG)).thenReturn(requestLog);
    }

    @Test
    void whenNoValidationError_thenSuccess() throws Exception {
        // When
        boolean result = interceptor.preHandle(request, response, null);

        // Then
        String expectedGeoLocationUrl = applicationConfig.getGeoLocationUrl() + request.getRemoteAddr();
        RequestLog expectedRequestLog = RequestLog.builder()
                .requestCountryCode(DEFUALT_RESPONSE.getCountryCode())
                .requestIpProvider(DEFUALT_RESPONSE.getIsp())
                .build();

        // then
        assertThat(result).isTrue();
        verify(restTemplate).getForEntity(eq(expectedGeoLocationUrl), eq(IpGeoLocationResponse.class));
        verify(request).setAttribute(eq(ATTR_REQUEST_LOG), eq(expectedRequestLog));
    }

    @Test
    void whenCountryCodeIsBlocked_thenThrowInvalidIspException() {
        // given
        doAnswer(invocation -> {
            Errors errors = errorsArgumentCaptor.getValue();
            errors.reject("countryCode", "error message");
            return null;
        }).when(requestCountryCodeValidator).validate(any(), errorsArgumentCaptor.capture());

        // when
        String expectedGeoLocationUrl = applicationConfig.getGeoLocationUrl() + request.getRemoteAddr();

        assertThatThrownBy(() -> interceptor.preHandle(request, response, null))
                .isInstanceOf(InvalidCountryCodeException.class);
        verify(restTemplate).getForEntity(eq(expectedGeoLocationUrl), eq(IpGeoLocationResponse.class));
        verify(request).setAttribute(eq(ATTR_REQUEST_LOG), any());
    }

    @Test
    void whenIspIsBlocked_thenThrowInvalidIspException() {
        // given
        doAnswer(invocation -> {
            Errors errors = errorsArgumentCaptor.getValue();
            errors.reject("isp", "error message");
            return null;
        }).when(requestIspValidator).validate(any(), errorsArgumentCaptor.capture());

        // when
        String expectedGeoLocationUrl = applicationConfig.getGeoLocationUrl() + request.getRemoteAddr();

        assertThatThrownBy(() -> interceptor.preHandle(request, response, null))
                .isInstanceOf(InvalidIspException.class);
        verify(restTemplate).getForEntity(eq(expectedGeoLocationUrl), eq(IpGeoLocationResponse.class));
        verify(request).setAttribute(eq(ATTR_REQUEST_LOG), any());
    }

}