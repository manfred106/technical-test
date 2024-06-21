package org.test.service;

import com.opencsv.CSVParserBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.test.domain.Person;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.test.exception.FileFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EntryFileService {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;


    public List<Person> getPersonList(MultipartFile file) throws IOException, FileFormatException {

        List<Person> personList = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), DEFAULT_CHARSET))) {

            CSVReaderBuilder csvReaderBuilder = new CSVReaderBuilder(reader)
                    .withCSVParser(new CSVParserBuilder()
                            .withSeparator('|')
                            .build()
                    );

            try {
                CSVReader csvReader = csvReaderBuilder.build();
                csvReader.forEach(record -> {

                    personList.add(Person.builder()
                            .uuid(UUID.fromString(record[0]))
                            .id(record[1])
                            .name(record[2])
                            .likes(record[3])
                            .transport(record[4])
                            .build());
                });
            }
            catch (NumberFormatException exception) {
                String message = String.format("Input file '%s' is not in correct format", file.getOriginalFilename());
                throw new FileFormatException(message, exception);
            }

        }

        return personList;
    }

}
