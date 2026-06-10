package ru.itlab.cloudjava.menuservice.storage.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Ingredient implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private int calories;
}
