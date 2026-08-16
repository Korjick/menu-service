package ru.itlab.cloudjava.menuservice.storage.repositories;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.itlab.cloudjava.menuservice.BaseTest;
import ru.itlab.cloudjava.menuservice.dto.SortBy;
import ru.itlab.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.itlab.cloudjava.menuservice.storage.model.Category;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem;
import ru.itlab.cloudjava.menuservice.storage.repositories.updaters.MenuAttrUpdaterConfig;
import ru.itlab.cloudjava.menuservice.testutils.TestData;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem_;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Import(MenuAttrUpdaterConfig.class)
@Transactional(propagation = Propagation.NEVER)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MenuItemRepositoryImplTest extends BaseTest {
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Test
    void updateMenu_updatesMenu_whenAllUpdateFieldsAreSet() {
        final var dto = TestData.updateMenuFullRequest();
        final var id = getIdByName("Cappuccino");
        final var updateCount = menuItemRepository.updateMenu(id, dto);
        assertThat(updateCount).isEqualTo(1);
        final var found = menuItemRepository.findById(id);
        assertThat(found).isPresent();
        final var updated = found.get();
        assertFieldsEquality(updated, dto,
                MenuItem_.NAME,
                MenuItem_.DESCRIPTION,
                MenuItem_.PRICE,
                MenuItem_.TIME_TO_COOK,
                MenuItem_.IMAGE_URL);
    }

    @ParameterizedTest(name = "[{index}] Проверка частичного обновления: {0}")
    @MethodSource("ru.itlab.cloudjava.menuservice.testutils.TestData#updateMenuPartialRequests")
    void updateMenu_updatesMenu_whenSomeUpdateFieldsAreSet(Map.Entry<String, UpdateMenuRequest> fieldRequestPair) {
        final var id = getIdByName("Cappuccino");
        final var updateCount = menuItemRepository.updateMenu(id, fieldRequestPair.getValue());
        assertThat(updateCount).isEqualTo(1);
        final var found = menuItemRepository.findById(id);
        assertThat(found).isPresent();
        final var updated = found.get();
        assertFieldsEquality(updated, fieldRequestPair.getValue(), fieldRequestPair.getKey());
    }

    @Test
    void updateMenu_throws_whenUpdateRequestHasNotUniqueName() {
        final var id = getIdByName("Cappuccino");
        final var dto = UpdateMenuRequest.builder()
                .name("Wine")
                .build();

        assertThatThrownBy(() -> menuItemRepository.updateMenu(id, dto))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void updateMenu_updatesNothing_whenNoMenuPresentInDB() {
        final var id = Long.MAX_VALUE;
        final var dto = TestData.updateMenuFullRequest();

        final var updateCount = menuItemRepository.updateMenu(id, dto);

        assertThat(updateCount).isZero();
        assertThat(menuItemRepository.findById(id)).isEmpty();
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByPriceAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.PRICE_ASC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Wine", "Tea"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByPriceDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.PRICE_DESC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Tea", "Wine", "Cappuccino"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByNameAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByNameDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.ZA);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Wine", "Tea", "Cappuccino"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByDateAsc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.DATE_ASC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Cappuccino", "Wine", "Tea"));
    }

    @Test
    void getMenusFor_returnsCorrectListForDRINKS_sortedByDateDesc() {
        var drinks = menuItemRepository.getMenusFor(Category.DRINKS, SortBy.DATE_DESC);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItem::getName, List.of("Tea", "Wine", "Cappuccino"));
    }
}
