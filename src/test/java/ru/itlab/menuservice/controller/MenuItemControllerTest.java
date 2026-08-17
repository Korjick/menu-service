package ru.itlab.menuservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.itlab.menuservice.dto.MenuItemDto;
import ru.itlab.menuservice.storage.model.Category;
import ru.itlab.menuservice.storage.model.Ingredient;
import ru.itlab.menuservice.storage.model.IngredientCollection;
import ru.itlab.menuservice.BaseIntegrationTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.itlab.menuservice.testutils.TestConstants.BASE_URL;
import static ru.itlab.menuservice.testutils.TestData.createMenuRequest;
import static ru.itlab.menuservice.testutils.TestData.updateMenuFullRequest;

public class MenuItemControllerTest extends BaseIntegrationTest {

    @Test
    void getMenu_returnsMenu_whenItExists() {
        var id = getIdByName("Cappuccino");
        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo("Cappuccino");
                });
    }

    @Test
    void getMenus_returnsEmptyListForCategoryNotPresentInDb() {
        webTestClient.get()
                .uri(BASE_URL + "?category=lunch&sort=az")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .value(response -> {
                    assertThat(response).isEmpty();
                });
    }

    @Test
    void getMenu_returnsNotFound_whenItemNotExists() {
        var id = 1000L;
        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getMenus_returnsCorrectListForDRINKS_sortedByAZ() {
        webTestClient.get()
                .uri(BASE_URL + "?category=drinks&sort=az")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .value(items -> {
                    assertThat(items).hasSize(3);
                    assertThat(items.get(0).getName()).isEqualTo("Cappuccino");
                    assertThat(items.get(1).getName()).isEqualTo("Tea");
                    assertThat(items.get(2).getName()).isEqualTo("Wine");
                });
    }

    @Test
    void createMenuItem_createsItem() {
        var dto = createMenuRequest();
        var now = LocalDateTime.now();

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo(dto.getName());
                    assertThat(response.getDescription()).isEqualTo(dto.getDescription());
                    assertThat(response.getPrice()).isEqualTo(dto.getPrice());
                    assertThat(response.getTimeToCook()).isEqualTo(dto.getTimeToCook());
                    assertThat(response.getImageUrl()).isEqualTo(dto.getImageUrl());
                    assertThat(response.getIngredientCollection()).isEqualTo(dto.getIngredientCollection());
                    assertThat(response.getCreatedAt()).isAfter(now);
                    assertThat(response.getUpdatedAt()).isAfter(now);
                });
    }

    @Test
    void createMenuItem_returnsConflict_whenMenuWithThatNameInDb() {
        var dto = createMenuRequest();
        dto.setName("Cappuccino");

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deleteMenuItem_deletesItem() {
        var id = getIdByName("Cappuccino");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateMenuItem_updatesItem() {
        var update = updateMenuFullRequest();
        var id = getIdByName("Cappuccino");

        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getName()).isEqualTo(update.getName());
                    assertThat(response.getPrice().compareTo(update.getPrice())).isEqualTo(0);
                    assertThat(response.getTimeToCook()).isEqualTo(update.getTimeToCook());
                    assertThat(response.getDescription()).isEqualTo(update.getDescription());
                    assertThat(response.getImageUrl()).isEqualTo(update.getImageUrl());
                });
    }

    @Test
    void updateMenuItem_returnsNotFound_whenItemNotInDb() {
        var update = updateMenuFullRequest();
        var id = 1000L;
        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getMenuItemById_returnsItem() {
        webTestClient.get()
                .uri(BASE_URL + "/" + getIdByName("Cappuccino"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo("Cappuccino");
                    assertThat(response.getDescription()).isEqualTo("Nice Coffee");
                    assertThat(response.getPrice().compareTo(BigDecimal.valueOf(10))).isEqualTo(0);
                    assertThat(response.getTimeToCook()).isEqualTo(1000);
                    assertThat(response.getImageUrl()).isEqualTo("http://images.com/cappuccino.png");
                    assertThat(response.getIngredientCollection()).isEqualTo(IngredientCollection.builder()
                            .ingredients(List.of(
                                    Ingredient.builder()
                                            .name("milk")
                                            .calories(10)
                                            .build(),
                                    Ingredient.builder()
                                            .name("water")
                                            .calories(0)
                                            .build(),
                                    Ingredient.builder()
                                            .name("coffe beans")
                                            .calories(100)
                                            .build()
                            ))
                            .build());
                });
    }

    @Test
    void getMenuItemById_returnsNotFound_whenItemNotInDb() {
        webTestClient.get()
                .uri(BASE_URL + "/" + 1000)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getEmptyMenuItemList_returnsEmptyList() {
        webTestClient.get()
                .uri(BASE_URL + "?category=" + Category.DINNER)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .hasSize(0);
    }

    @Test
    void getMenuItemList_returnsCorrectSortedList() {
        webTestClient.get()
                .uri(BASE_URL + "?category=" + Category.SALADS)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .value(response -> {
                    assertThat(response).hasSize(2);
                    assertThat(response.get(0).getName()).isEqualTo("Georgian Salad");
                    assertThat(response.get(1).getName()).isEqualTo("Green Salad");
                });
    }
}
