package org.test.domain;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "uuid")
public class Person {

    public interface PublicFields {
    }

    private UUID uuid;

    private String id;

    @JsonView(PublicFields.class)
    private String name;

    private String likes;

    @JsonView(PublicFields.class)
    private String transport;

    private Double avgSpeed;

    @JsonView(PublicFields.class)
    private Double topSpeed;

}