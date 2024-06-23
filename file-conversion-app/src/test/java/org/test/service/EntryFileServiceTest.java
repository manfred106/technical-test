package org.test.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.test.domain.Person;
import org.test.exception.InvalidFileException;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntryFileServiceTest {

    private static final String DEFAULT_UUID = "18148426-89e1-11ee-b9d1-0242ac120002";
    private static final String DEFAULT_ID = "1X1D14";
    private static final String DEFAULT_NAME = "John Smith";
    private static final String DEFAULT_LIKE = "Likes Apricots";
    private static final String DEFAULT_TRANSPORT = "Rides A Bike";
    private static final Double DEFAULT_AVG_SPEED = 6.2;
    private static final Double DEFAULT_TOP_SPEED = 15.3;

    private static final String VALID_FILE_CONTENT = String.format("%s|%s|%s|%s|%s|%s|%s", DEFAULT_UUID, DEFAULT_ID, DEFAULT_NAME, DEFAULT_LIKE, DEFAULT_TRANSPORT, DEFAULT_AVG_SPEED, DEFAULT_TOP_SPEED);
    private static final String INVALID_FILE_CONTENT_EMPTY_FIELD = String.format("%s|%s|%s|%s|%s|%s|%s", DEFAULT_UUID, DEFAULT_ID, DEFAULT_NAME, DEFAULT_LIKE, "", DEFAULT_AVG_SPEED, DEFAULT_TOP_SPEED);
    private static final String INVALID_FILE_CONTENT_NON_NUMERIC = String.format("%s|%s|%s|%s|%s|%s|%s", DEFAULT_UUID, DEFAULT_ID, DEFAULT_NAME, DEFAULT_LIKE, DEFAULT_TRANSPORT, "Not a number", DEFAULT_TOP_SPEED);

    @Mock
    private Validator validator;

    @Mock
    private ConstraintViolation<Person> constraintViolation;

    @InjectMocks
    private EntryFileService service;

    @Test
    void whenInputValidMultipartFile_thenReturnPersonList() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("test-file", "test-file.txt", "text/plain", VALID_FILE_CONTENT.getBytes());

        // when
        List<Person> personList = service.getPersonList(multipartFile);

        // then
        assertThat(personList).hasSize(1);

        Person person = personList.get(0);
        assertThat(person.getUuid()).isEqualTo(UUID.fromString(DEFAULT_UUID));
        assertThat(person.getId()).isEqualTo(DEFAULT_ID);
        assertThat(person.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(person.getLike()).isEqualTo(DEFAULT_LIKE);
        assertThat(person.getTransport()).isEqualTo(DEFAULT_TRANSPORT);
        assertThat(person.getAvgSpeed()).isEqualTo(Double.valueOf(DEFAULT_AVG_SPEED));
        assertThat(person.getTopSpeed()).isEqualTo(Double.valueOf(DEFAULT_TOP_SPEED));
    }

    @Test
    void whenInputInvalidMultipartFileWithEmptyField_thenThrowInvalidFileException() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("test-file", "test-file.txt", "text/plain", INVALID_FILE_CONTENT_EMPTY_FIELD.getBytes());
        when(validator.validate(any(Person.class))).thenReturn(Set.of(constraintViolation));

        // when
        assertThatThrownBy(() -> service.getPersonList(multipartFile)).isInstanceOf(InvalidFileException.class);
    }

    @Test
    void whenInputInvalidMultipartFileWithNonNumericField_thenThrowInvalidFileException() throws IOException {
        // given
        MultipartFile multipartFile = new MockMultipartFile("test-file", "test-file.txt", "text/plain", INVALID_FILE_CONTENT_NON_NUMERIC.getBytes());

        // when
        assertThatThrownBy(() -> service.getPersonList(multipartFile)).isInstanceOf(InvalidFileException.class);
    }

}