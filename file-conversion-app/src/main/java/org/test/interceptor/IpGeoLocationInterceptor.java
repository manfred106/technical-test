package org.test.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
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

@Slf4j
@Component
@EnableConfigurationProperties(ApplicationConfig.class)
public class IpGeoLocationInterceptor implements HandlerInterceptor {

    private static final String ATTR_REQUEST_LOG = "request.requestLog";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RequestCountryCodeValidator requestCountryCodeValidator;

    @Autowired
    private RequestIspValidator requestIspValidator;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        //String requestURL = String.format("http://ip-api.com/json/%s", request.getRemoteAddr());
        String requestURL = String.format("http://ip-api.com/json/%s", "218.102.32.232");

        ResponseEntity<IpGeoLocationResponse> responseEntity = restTemplate.getForEntity(requestURL, IpGeoLocationResponse.class);

        RequestLog requestLog = (RequestLog) request.getAttribute(ATTR_REQUEST_LOG);
        requestLog.withRequestCountryCode(responseEntity.getBody().getCountryCode())
                .withRequestIpProvider(responseEntity.getBody().getIsp());

        IpGeoLocationResponse responseBody = responseEntity.getBody();
        validateCountryCode(responseBody);
        validateIsp(responseBody);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

    private void validateCountryCode(IpGeoLocationResponse response) {
        Errors errors = new BeanPropertyBindingResult(response, response.getClass().getName());
        requestCountryCodeValidator.validate(response, errors);
        if (errors.hasErrors()) {
            throw new InvalidCountryCodeException(errors.getAllErrors().get(0).getDefaultMessage());
        }
    }

    private void validateIsp(IpGeoLocationResponse response) {
        Errors errors = new BeanPropertyBindingResult(response, response.getClass().getName());
        requestIspValidator.validate(response, errors);
        if (errors.hasErrors()) {
            throw new InvalidIspException(errors.getAllErrors().get(0).getDefaultMessage());
        }
    }

}