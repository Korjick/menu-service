package ru.itlab.cloudjava.menuservice.storage.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class IngredientCollection implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private List<Ingredient> ingredients;
}
