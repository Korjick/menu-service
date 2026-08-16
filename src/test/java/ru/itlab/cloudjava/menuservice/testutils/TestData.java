package ru.itlab.cloudjava.menuservice.testutils;

import ru.itlab.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.itlab.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.itlab.cloudjava.menuservice.storage.model.Category;
import ru.itlab.cloudjava.menuservice.storage.model.Ingredient;
import ru.itlab.cloudjava.menuservice.storage.model.IngredientCollection;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem_;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static ru.itlab.cloudjava.menuservice.testutils.TestConstants.*;


public class TestData {

    public static IngredientCollection italianSaladIngredients() {
        return new IngredientCollection(
                List.of(
                        new Ingredient(ITALIAN_SALAD_GREENS_INGREDIENT, ITALIAN_SALAD_GREENS_INGREDIENT_CALORIES),
                        new Ingredient(ITALIAN_SALAD_TOMATOES_INGREDIENT, ITALIAN_SALAD_TOMATOES_INGREDIENT_CALORIES)
                )
        );
    }

    public static UpdateMenuRequest updateMenuFullRequest() {
        return UpdateMenuRequest.builder()
                .name(ESPRESSO_NAME)
                .price(ESPRESSO_PRICE)
                .timeToCook(ESPRESSO_TIME_TO_COOK)
                .description(ESPRESSO_DESCRIPTION)
                .imageUrl(ESPRESSO_IMAGE_URL)
                .build();
    }

    public static Stream<Map.Entry<String, UpdateMenuRequest>> updateMenuPartialRequests() {
        return Stream.of(
                Map.entry(MenuItem_.NAME, UpdateMenuRequest.builder().name(ESPRESSO_NAME).build()),
                Map.entry(MenuItem_.PRICE, UpdateMenuRequest.builder().price(ESPRESSO_PRICE).build()),
                Map.entry(MenuItem_.TIME_TO_COOK, UpdateMenuRequest.builder().timeToCook(ESPRESSO_TIME_TO_COOK).build()),
                Map.entry(MenuItem_.DESCRIPTION, UpdateMenuRequest.builder().description(ESPRESSO_DESCRIPTION).build()),
                Map.entry(MenuItem_.IMAGE_URL, UpdateMenuRequest.builder().imageUrl(ESPRESSO_IMAGE_URL).build())
        );
    }

    public static CreateMenuRequest createMenuRequest() {
        return CreateMenuRequest.builder()
                .name(ITALIAN_SALAD_NAME)
                .description(ITALIAN_SALAD_DESCRIPTION)
                .price(ITALIAN_SALAD_PRICE)
                .category(Category.SALADS)
                .timeToCook(ITALIAN_SALAD_TIME_TO_COOK)
                .weight(ITALIAN_SALAD_WEIGHT)
                .imageUrl(ITALIAN_SALAD_IMAGE_URL)
                .ingredientCollection(italianSaladIngredients())
                .build();
    }
}
