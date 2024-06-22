package org.test.controller;

import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.test.domain.Person;
import org.test.exception.InvalidFileException;
import org.test.service.EntryFileService;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileConversionControllerTest {

    private static final byte[] DEFAULT_CONTENT = "default|file|content".getBytes();

    @Mock
    private EntryFileService entryFileService;

    @InjectMocks
    private FileConversionController controller;

    @Test
    void whenProcessFile_thenReturnOK() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test-file", "test-file.txt", "text/plain", DEFAULT_CONTENT);
        List<Person> dummyPersonList = List.of(Person.builder().build());
        when(entryFileService.getPersonList(any())).thenReturn(dummyPersonList);

        // when
        ResponseEntity<List<Person>> responseEntity = controller.processFile(multipartFile);

        // then
        verify(entryFileService).getPersonList(multipartFile);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION)).isEqualTo("attachment; filename=OutcomeFile.json");
        assertThat(responseEntity.getBody()).isEqualTo(dummyPersonList);
    }

    @Test
    void whenProcessEmptyFile_thenThrowInvalidFileException() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test-file", "test-file.txt", "text/plain", new byte[] {});

        // then
        assertThatThrownBy(() -> controller.processFile(multipartFile)).isInstanceOf(InvalidFileException.class);
        verify(entryFileService, never()).getPersonList(multipartFile);
    }

    @Test
    void whenProcessInvalidFileExtension_thenThrowInvalidFileException() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test-file", "test-file.csv", "text/plain", DEFAULT_CONTENT);

        // then
        assertThatThrownBy(() -> controller.processFile(multipartFile)).isInstanceOf(InvalidFileException.class);
        verify(entryFileService, never()).getPersonList(multipartFile);
    }


    @Test
    void whenEntryFileThrowsInvalidFileException_thenThrowInvalidFileException() throws IOException {
        // given
        MockMultipartFile multipartFile = new MockMultipartFile("test-file", "test-file.txt", "text/plain", DEFAULT_CONTENT);
        when(entryFileService.getPersonList(multipartFile)).thenThrow(new InvalidFileException("Input file is not in correct format", new CsvException()));

        // then
        assertThatThrownBy(() -> controller.processFile(multipartFile)).isInstanceOf(InvalidFileException.class);
        verify(entryFileService).getPersonList(multipartFile);
    }

}