package ru.itlab.cloudjava.menuservice.storage.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Ingredient {
    private String name;
    private int calories;
}
