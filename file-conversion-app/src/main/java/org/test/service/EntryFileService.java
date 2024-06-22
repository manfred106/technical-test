package org.test.service;

import com.opencsv.CSVParserBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.domain.Person;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.test.exception.InvalidFileException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class EntryFileService {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Autowired
    private Validator validator;

    public List<Person> getPersonList(MultipartFile file) throws IOException {

        List<Person> personList = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), DEFAULT_CHARSET))) {

            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader)
                    .withCSVParser(new CSVParserBuilder()
                            .withSeparator('|')
                            .build()
                    );

            try (CSVReader csvReader = csvReaderBuilder.build()) {

                int row = 0;
                for (String[] record : csvReader) {
                    row++;
                    Person person = Person.builder()
                            .uuid(UUID.fromString(record[0]))
                            .id(record[1])
                            .name(record[2])
                            .like(record[3])
                            .transport(record[4])
                            .avgSpeed(Double.valueOf(record[5]))
                            .topSpeed(Double.valueOf(record[6]))
                            .build();

                    Set<ConstraintViolation<Person>> violations = validator.validate(person);
                    if (!violations.isEmpty()) {
                        String errorMessage = buildErrorMessageFromViolations(row, violations);
                        throw new InvalidFileException(errorMessage);
                    }

                    personList.add(person);
                }
            }
            catch (NumberFormatException exception) {
                String message = String.format("Input file '%s' is not in correct format", file.getOriginalFilename());
                throw new InvalidFileException(message, exception);
            }

        }

        return personList;
    }

    private String buildErrorMessageFromViolations(int row, Set<ConstraintViolation<Person>> violations) {
        StringBuilder errorMessage = new StringBuilder("Validation errors at row " + row + ": ");
        for (ConstraintViolation<Person> violation : violations) {
            errorMessage.append(violation.getPropertyPath())
                    .append(" ")
                    .append(violation.getMessage());
        }
        return errorMessage.toString();
    }

}