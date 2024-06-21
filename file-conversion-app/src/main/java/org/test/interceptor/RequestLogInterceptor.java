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

@Slf4j
@Component
public class RequestLogInterceptor implements HandlerInterceptor {

    static final String ATTR_REQUEST_LOG = "request.requestLog";

    @Autowired
    private RequestLogRepository requestLogRepository;

    @Autowired
    private LocalDateTimeProvider localDateTimeProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler)
            throws Exception {
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
                           Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex)
            throws Exception {
        RequestLog requestLog = (RequestLog) request.getAttribute(ATTR_REQUEST_LOG);
        requestLog = requestLog.withResponseCode(response.getStatus())
                .withTimeLapsed(ChronoUnit.MILLIS.between(requestLog.getRequestTimestamp(), localDateTimeProvider.getCurrentDateTime()));

        log.info("RequestLogInterceptor afterCompletion requestLog={}", requestLog);
        requestLogRepository.save(requestLog);
    }


}

