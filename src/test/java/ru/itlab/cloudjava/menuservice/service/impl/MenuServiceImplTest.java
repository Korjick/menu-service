package ru.itlab.cloudjava.menuservice.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.itlab.cloudjava.menuservice.BaseTest;
import ru.itlab.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.itlab.cloudjava.menuservice.dto.MenuItemDto;
import ru.itlab.cloudjava.menuservice.dto.SortBy;
import ru.itlab.cloudjava.menuservice.exception.MenuServiceException;
import ru.itlab.cloudjava.menuservice.service.MenuService;
import ru.itlab.cloudjava.menuservice.storage.model.Category;
import ru.itlab.cloudjava.menuservice.storage.repositories.MenuItemRepository;
import ru.itlab.cloudjava.menuservice.testutils.TestConstants;
import ru.itlab.cloudjava.menuservice.testutils.TestData;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
public class MenuServiceImplTest extends BaseTest {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuItemRepository repository;

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
        var result = menuService.createMenuItem(dto);
        assertMenuItemValid(result, dto, now);
    }

    @Test
    void getMenuItemById_returnsMenuItem() {
        var dto = TestData.createMenuRequest();
        var now = getNormalizedNow();
        var savedDto = menuService.createMenuItem(dto);
        var result = menuService.getMenu(savedDto.getId());
        assertMenuItemValid(result, dto, now);
    }

    @Test
    void getMenuItemById_returnsException_whenNotFound() {
        assertThatExceptionOfType(MenuServiceException.class)
                .isThrownBy(() -> menuService.getMenu(-1L));
    }

    @Test
    void deleteMenuItemById() {
        var dto = TestData.createMenuRequest();
        var now = getNormalizedNow();
        var result = menuService.createMenuItem(dto);
        assertMenuItemValid(result, dto, now);
        menuService.deleteMenuItem(result.getId());
        assertThatExceptionOfType(MenuServiceException.class)
                .isThrownBy(() -> menuService.getMenu(result.getId()));
    }

    @Test
    void createMenuItem_exceptionWhenRepeatableName() {
        var dto = TestData.createMenuRequest();
        var now = getNormalizedNow();
        var result = menuService.createMenuItem(dto);
        assertMenuItemValid(result, dto, now);
        assertThatExceptionOfType(MenuServiceException.class).isThrownBy(() -> menuService.createMenuItem(dto));
    }

    @Test
    void updateMenuItem_exceptionWhenNotFound() {
        assertThatExceptionOfType(MenuServiceException.class).isThrownBy(() -> menuService.updateMenuItem(-1L,
                TestData.updateMenuFullRequest()));
    }

    @Test
    void updateMenuItem_exceptionWhenRepeatableName() {
        var dtoBase = TestData.createMenuRequest();
        dtoBase.setName(TestConstants.ITALIAN_SALAD_NAME);
        var now = getNormalizedNow();
        var resultBase = menuService.createMenuItem(dtoBase);
        assertMenuItemValid(resultBase, dtoBase, now);
        var dtoRepeatable = TestData.createMenuRequest();
        dtoRepeatable.setName(TestConstants.ESPRESSO_NAME);
        var resultRepeatable = menuService.createMenuItem(dtoRepeatable);
        assertMenuItemValid(resultRepeatable, dtoRepeatable, now);
        var updateRequest = TestData.updateMenuFullRequest();
        updateRequest.setName(TestConstants.ITALIAN_SALAD_NAME);
        assertThatExceptionOfType(MenuServiceException.class).isThrownBy(() ->
                menuService.updateMenuItem(resultRepeatable.getId(), updateRequest));
    }

    private void assertMenuItemValid(MenuItemDto result, CreateMenuRequest expectedDto, LocalDateTime exactTimeFrom) {
        assertThat(result.getId()).isNotNull();
        assertFieldsEquality(result, expectedDto, "name", "description", "price", "imageUrl", "timeToCook");

        if (exactTimeFrom != null) {
            assertThat(result.getCreatedAt()).isAfter(exactTimeFrom);
            assertThat(result.getUpdatedAt()).isAfter(exactTimeFrom);
        }
    }
}
