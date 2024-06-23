package org.test.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.test.config.ApplicationConfig;
import org.test.entity.RequestLog;
import org.test.exception.InvalidCountryCodeException;
import org.test.exception.InvalidIspException;
import org.test.validator.RequestCountryCodeValidator;
import org.test.validator.RequestIspValidator;

import java.util.function.Function;

import static org.test.interceptor.RequestAttributeConstant.ATTR_REQUEST_LOG;

@Slf4j
@Component
@EnableConfigurationProperties(ApplicationConfig.class)
public class IpGeoLocationInterceptor implements HandlerInterceptor {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RequestCountryCodeValidator requestCountryCodeValidator;

    @Autowired
    private RequestIspValidator requestIspValidator;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (Boolean.TRUE.equals(applicationConfig.getGeoLocationValidation())) {
            String remoteAddr = request.getRemoteAddr();
            String requestURL = applicationConfig.getGeoLocationUrl() + remoteAddr;

            ResponseEntity<IpGeoLocationResponse> responseEntity = restTemplate.getForEntity(requestURL, IpGeoLocationResponse.class);
            log.debug("remoteAddr={}, responseEntity={}", remoteAddr, responseEntity);

            RequestLog requestLog = (RequestLog) request.getAttribute(ATTR_REQUEST_LOG);
            request.setAttribute(ATTR_REQUEST_LOG, requestLog
                    .withRequestCountryCode(responseEntity.getBody().getCountryCode())
                    .withRequestIpProvider(responseEntity.getBody().getIsp()));

            IpGeoLocationResponse responseBody = responseEntity.getBody();
            validateCountryCode(responseBody);
            validateIsp(responseBody);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    }

    private void validateCountryCode(IpGeoLocationResponse response) throws Exception {
        validateField(response, requestCountryCodeValidator, InvalidCountryCodeException::new);
    }

    private void validateIsp(IpGeoLocationResponse response) throws Exception {
        validateField(response, requestIspValidator, InvalidIspException::new);
    }

    private void validateField(IpGeoLocationResponse response, Validator validator, Function<String, Exception> exceptionFunction) throws Exception {
        Errors errors = new BeanPropertyBindingResult(response, response.getClass().getName());
        validator.validate(response, errors);
        if (errors.hasErrors()) {
            throw exceptionFunction.apply(errors.getAllErrors().get(0).getDefaultMessage());
        }
    }

}