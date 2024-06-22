package org.test.domain;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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

    @NotNull
    private UUID uuid;

    @NotEmpty
    private String id;

    @JsonView(PublicFields.class)
    @NotEmpty
    private String name;

    @NotEmpty
    private String like;

    @JsonView(PublicFields.class)
    @NotEmpty
    private String transport;

    @NotNull
    @PositiveOrZero
    private Double avgSpeed;

    @JsonView(PublicFields.class)
    @NotNull
    @PositiveOrZero
    private Double topSpeed;

}