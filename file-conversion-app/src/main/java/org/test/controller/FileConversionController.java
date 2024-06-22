package org.test.controller;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import org.test.domain.Person;
import org.test.exception.InvalidFileException;
import org.test.service.EntryFileService;

@RestController
@RequestMapping("/file/")
@Slf4j
public class FileConversionController {

    @Autowired
    private EntryFileService entryFileService;

    @PostMapping("/convert")
    @JsonView(Person.PublicFields.class)
    public ResponseEntity<List<Person>> processFile(@RequestParam("file") MultipartFile file)
            throws IOException {

        if (file.isEmpty()) {
            throw new InvalidFileException("No file found");
        }

        String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (!"txt".equalsIgnoreCase(fileExtension)) {
            throw new InvalidFileException(String.format("%s is not a valid file extension", fileExtension));
        }

        List<Person> personList = entryFileService.getPersonList(file);
        log.debug("Rows of data={}", CollectionUtils.size(personList));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=OutcomeFile.json")
                .body(personList);
    }

}
