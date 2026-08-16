package ru.itlab.cloudjava.menuservice.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itlab.cloudjava.menuservice.BaseTest;
import ru.itlab.cloudjava.menuservice.dto.MenuItemDto;
import ru.itlab.cloudjava.menuservice.dto.SortBy;
import ru.itlab.cloudjava.menuservice.exception.MenuServiceException;
import ru.itlab.cloudjava.menuservice.service.MenuService;
import ru.itlab.cloudjava.menuservice.storage.model.Category;
import ru.itlab.cloudjava.menuservice.storage.repositories.MenuItemRepository;
import ru.itlab.cloudjava.menuservice.testutils.TestData;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class MenuServiceImplTest extends BaseTest {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Test
    void getMenusFor_DRINKS_returnsCorrectList() {
        List<MenuItemDto> drinks = menuService.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItemDto::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void createMenuItem_createsMenuItem() {
        var dto = TestData.createMenuRequest();
        var now = getNormalizedNow();
        MenuItemDto result = menuService.createMenuItem(dto);
        assertThat(result.getId()).isNotNull();
        assertFieldsEquality(result, dto, "name", "description", "price", "imageUrl", "timeToCook");
        assertThat(result.getCreatedAt()).isAfter(now);
        assertThat(result.getUpdatedAt()).isAfter(now);
    }

    @Test
    void getMenu_returnsMenu_whenMenuInDb() {
        var id = getIdByName("Cappuccino");
        var menu = menuService.getMenu(id);
        assertThat(menu).isNotNull();
        assertThat(menu.getName()).isEqualTo("Cappuccino");
        assertThat(menu.getId()).isNotNull();
        assertThat(menu.getCreatedAt()).isNotNull();
        assertThat(menu.getUpdatedAt()).isNotNull();
    }

    @Test
    void getMenu_throws_whenNoMenuInDb() {
        assertThrows(
                MenuServiceException.class,
                () -> menuService.getMenu(1000L)
        );
    }

    @Test
    void deleteMenuItem_deletesItem() {
        var id = getIdByName("Cappuccino");
        menuService.deleteMenuItem(id);
        var deletedOpt = menuItemRepository.findById(id);
        assertThat(deletedOpt).isEmpty();
    }


    @Test
    void createMenuItem_throwsWhenItemWithThatNameExists() {
        var dto = TestData.createMenuRequest();
        dto.setName("Cappuccino");
        assertThrows(
                MenuServiceException.class,
                () -> menuService.createMenuItem(dto)
        );
    }

    @Test
    void updateMenuItem_updatesMenuItem_whenItemPresentInDb() {
        var id = getIdByName("Cappuccino");
        var update = TestData.updateMenuFullRequest();
        MenuItemDto updated = menuService.updateMenuItem(id, update);
        assertFieldsEquality(updated, update, "name", "description", "price", "timeToCook", "imageUrl");
    }

    @Test
    void updateMenuItem_throws_whenNoItemInDb() {
        var id = 1000L;
        var update = TestData.updateMenuFullRequest();
        assertThrows(
                MenuServiceException.class,
                () -> menuService.updateMenuItem(id, update)
        );
    }

    @Test
    void updateMenuItem_throws_whenUpdateRequestContainsNotUniqueName() {
        var id = getIdByName("Cappuccino");
        var update = TestData.updateMenuFullRequest();
        update.setName("Wine");
        assertThrows(MenuServiceException.class,
                () -> menuService.updateMenuItem(id, update));
    }
}
