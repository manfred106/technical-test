package org.test.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.test.config.ApplicationConfig;
import org.test.interceptor.IpGeoLocationResponse;
import org.test.repository.RequestLogRepository;
import wiremock.org.eclipse.jetty.http.HttpStatus;

import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integrationTest")
class FileConversionControllerIntegrationTest {

    private static final String BASE_URL = "/file/convert";

    private static final byte[] DEFAULT_VALID_FILE_CONTENT = new StringBuilder()
            .append("18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|6.2|12.1\n")
            .append("3ce2d17b-e66a-4c1e-bca3-40eb1c9222c7|2X2D24|Mike Smith|Likes Grape|Drives an SUV|35.0|95.5\n")
            .toString()
            .getBytes();

    private static final byte[] DEFAULT_INVALID_FILE_CONTENT = new StringBuilder()
            .append("18148426-89e1-11ee-b9d1-0242ac120002|1X1D14|John Smith|Likes Apricots|Rides A Bike|Not a number|12.1\n")
            .toString()
            .getBytes();

    private static final IpGeoLocationResponse DEFAULT_GEO_LOCATION_RESPONSE = IpGeoLocationResponse.builder()
            .query("203.198.23.70")
            .status("success")
            .country("Hong Kong")
            .countryCode("HK")
            .region("HCW")
            .regionName("Central and Western District")
            .city("Central")
            .zip("96521")
            .lat(22.2836)
            .lon(114.16)
            .timezone("Asia/Hong_Kong")
            .isp("Hong Kong Telecommunications (HKT) Limited Mass Internet")
            .org("Hong Kong Telecommunications (HKT) Limited")
            .as("AS4760 HKT Limited")
            .build();

    private static WireMockServer wireMockServer;

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RequestLogRepository requestLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;


    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(); // Replace port with your desired port number
        wireMockServer.start(); // Start the WireMock server
    }

    @BeforeEach
    void setup() {
        String testGeoLocationUrl = String.format("http://localhost:%s/json/", wireMockServer.port());
        applicationConfig.setGeoLocationValidation(true);
        applicationConfig.setGeoLocationUrl(testGeoLocationUrl);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private void stubDefaultGeoLocation(IpGeoLocationResponse ipGeoLocationResponse) throws JsonProcessingException {
        String responseBody = objectMapper.writeValueAsString(ipGeoLocationResponse);
        wireMockServer.stubFor(get(urlPathMatching("/json/.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("content-type", "application/json")
                        .withBody(responseBody)));
    }

    private MockMultipartFile getValidMultipartFile() {
        return new MockMultipartFile("file", "EntryFile.txt", "text/plain", DEFAULT_VALID_FILE_CONTENT);
    }

    private MockMultipartFile getInvalidMultipartFile() {
        return new MockMultipartFile("file", "EntryFile.txt", "text/plain", DEFAULT_INVALID_FILE_CONTENT);
    }

    @Test
    void whenGeoLocationReturnDefault_thenReturnOk() throws Exception {
        // given
        stubDefaultGeoLocation(DEFAULT_GEO_LOCATION_RESPONSE);
        MockMultipartFile multipartFile = getValidMultipartFile();

        // when & then
        performAndAssertResult(multipartFile, HttpStatus.OK_200);
    }

    @ParameterizedTest
    @ValueSource(strings = {"CN", "ES", "US"})
    void whenGeoLocationReturnBlockedCountryCode_thenReturnForbidden(String countryCode) throws Exception {
        // given
        IpGeoLocationResponse ipGeoLocationResponse = DEFAULT_GEO_LOCATION_RESPONSE.withCountryCode(countryCode);
        stubDefaultGeoLocation(ipGeoLocationResponse);
        MockMultipartFile multipartFile = getValidMultipartFile();

        // when & then
        performAndAssertResult(multipartFile, HttpStatus.FORBIDDEN_403);
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void whenGeoLocationValidationDisabled_thenReturnOk(String countryCode, String isp) throws Exception {
        // given
        applicationConfig.setGeoLocationValidation(false);
        IpGeoLocationResponse ipGeoLocationResponse = DEFAULT_GEO_LOCATION_RESPONSE.withCountryCode(countryCode);
        stubDefaultGeoLocation(ipGeoLocationResponse);
        MockMultipartFile multipartFile = getValidMultipartFile();

        // when & then
        performAndAssertResult(multipartFile, HttpStatus.OK_200);
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                Arguments.of("CN", "valid-isp"),
                Arguments.of("FR", "GCP"),
                Arguments.of("US", "Azure")
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"AWS", "Azure", "GCP"})
    void whenGeoLocationReturnBlockedIsp_thenReturnForbidden(String isp) throws Exception {
        // given
        IpGeoLocationResponse ipGeoLocationResponse = DEFAULT_GEO_LOCATION_RESPONSE.withIsp(isp);
        stubDefaultGeoLocation(ipGeoLocationResponse);
        MockMultipartFile multipartFile = getValidMultipartFile();

        // when & then
        performAndAssertResult(multipartFile, HttpStatus.FORBIDDEN_403);
    }

    @Test
    void whenInvalidFileProvided_thenReturnOk() throws Exception {
        // given
        stubDefaultGeoLocation(DEFAULT_GEO_LOCATION_RESPONSE);
        MockMultipartFile multipartFile = getInvalidMultipartFile();

        // when & then
        performAndAssertResult(multipartFile, HttpStatus.BAD_REQUEST_400);
    }

    private void performAndAssertResult(MockMultipartFile multipartFile, int expectedStatusCode) throws Exception {
        long beforeLogCount = requestLogRepository.count();
        mockMvc.perform(MockMvcRequestBuilders.multipart(BASE_URL)
                        .file(multipartFile))
                .andExpect(status().is(expectedStatusCode));

        // assert a new log has been inserted to the db
        assertThat(requestLogRepository.count() - beforeLogCount).isEqualTo(1);
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.shutdown();
    }

}