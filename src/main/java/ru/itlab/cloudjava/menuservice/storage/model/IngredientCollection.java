package ru.itlab.cloudjava.menuservice.storage.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class IngredientCollection {
    private List<Ingredient> ingredients;
}
