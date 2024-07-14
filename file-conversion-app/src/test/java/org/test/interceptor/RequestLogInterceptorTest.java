package org.test.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.test.entity.RequestLog;
import org.test.repository.RequestLogRepository;
import org.test.util.LocalDateTimeProvider;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.test.interceptor.RequestAttributeConstant.ATTR_REQUEST_LOG;

@ExtendWith(MockitoExtension.class)
class RequestLogInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private RequestLogRepository requestLogRepository;

    @Mock
    private LocalDateTimeProvider localDateTimeProvider;

    @InjectMocks
    private RequestLogInterceptor interceptor;

    @Captor
    private ArgumentCaptor<RequestLog> requestLogCaptor;

    private static final LocalDateTime NOW = LocalDateTime.of(2024, 1, 1, 0, 0, 0);

    @Test
    void testPreHandle() {
        // given
        String requestUri = "/test";
        String remoteAddr = "127.0.0.1";
        when(request.getRemoteAddr()).thenReturn(remoteAddr);
        when(request.getRequestURI()).thenReturn(requestUri);
        when (localDateTimeProvider.getCurrentDateTime()).thenReturn(NOW);

        // when
        boolean result = interceptor.preHandle(request, response, null);

        // then
        assertThat(result).isTrue();
        verify(request).setAttribute(eq(ATTR_REQUEST_LOG), requestLogCaptor.capture());
        RequestLog capturedRequestLog = requestLogCaptor.getValue();
        assertThat(capturedRequestLog).isNotNull();
        assertThat(capturedRequestLog.getId()).isNotNull();
        assertThat(capturedRequestLog.getRequestIpAddress()).isEqualTo(remoteAddr);
        assertThat(capturedRequestLog.getRequestUri()).isEqualTo(requestUri);
        assertThat(capturedRequestLog.getRequestTimestamp()).isEqualTo(NOW);
    }

    @Test
    public void testAfterCompletion() {
        // given
        int statusCode = HttpStatus.OK.value();
        when(localDateTimeProvider.getCurrentDateTime()).thenReturn(NOW);
        RequestLog requestLog = new RequestLog()
                .withId(UUID.randomUUID())
                .withRequestTimestamp(localDateTimeProvider.getCurrentDateTime().minusSeconds(1));
        when(request.getAttribute(ATTR_REQUEST_LOG)).thenReturn(requestLog);
        when(response.getStatus()).thenReturn(statusCode);

        // when
        interceptor.afterCompletion(request, response, null, null);

        // then
        verify(requestLogRepository).save(requestLogCaptor.capture());
        RequestLog capturedRequestLog = requestLogCaptor.getValue();
        assertThat(capturedRequestLog).isNotNull();
        assertThat(capturedRequestLog.getResponseCode()).isEqualTo(statusCode);
        assertThat(capturedRequestLog.getTimeLapsed()).isEqualTo(1000);
    }

}
