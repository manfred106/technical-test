package org.test.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.test.entity.RequestLog;
import org.test.repository.RequestLogRepository;
import org.test.util.LocalDateTimeProvider;

import static org.test.interceptor.RequestAttributeConstant.ATTR_REQUEST_LOG;

@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    @Autowired
    private RequestLogRepository requestLogRepository;

    @Autowired
    private LocalDateTimeProvider localDateTimeProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) {
        RequestLog requestLog = new RequestLog()
                .withId(UUID.randomUUID())
                .withRequestIpAddress(request.getRemoteAddr())
                .withRequestUri(request.getRequestURI())
                .withRequestTimestamp(localDateTimeProvider.getCurrentDateTime());
        request.setAttribute(ATTR_REQUEST_LOG, requestLog);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        RequestLog requestLog = (RequestLog) request.getAttribute(ATTR_REQUEST_LOG);
        requestLog = requestLog.withResponseCode(response.getStatus())
                .withTimeLapsed(ChronoUnit.MILLIS.between(requestLog.getRequestTimestamp(), localDateTimeProvider.getCurrentDateTime()));

        log.debug("Going to save requestLog={}", requestLog);
        requestLogRepository.save(requestLog);
    }

}