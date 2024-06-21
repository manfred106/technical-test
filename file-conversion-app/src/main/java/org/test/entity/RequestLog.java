package org.test.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "request_log")
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class RequestLog {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "request_uri", nullable = false)
    private String requestUri;

    @Column(name = "request_timestamp", nullable = false)
    private LocalDateTime requestTimestamp;

    @Column(name = "response_code", nullable = false)
    private int responseCode;

    @Column(name = "request_ip_address", nullable = false)
    private String requestIpAddress;

    @Column(name = "request_country_code")
    private String requestCountryCode;

    @Column(name = "request_ip_provider")
    private String requestIpProvider;

    @Column(name = "time_lapsed", nullable = false)
    private long timeLapsed;

}
