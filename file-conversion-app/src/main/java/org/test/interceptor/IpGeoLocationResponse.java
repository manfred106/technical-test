package org.test.interceptor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IpGeoLocationResponse {

    private String query;
    private String status;
    private String country;
    private String countryCode;
    private String region;
    private String regionName;
    private String city;
    private String zip;
    private Double lat;
    private Double lon;
    private String timezone;
    private String isp;
    private String org;
    private String as;

}
