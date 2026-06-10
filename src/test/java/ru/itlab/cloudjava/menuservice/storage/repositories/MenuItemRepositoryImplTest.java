package ru.itlab.cloudjava.menuservice.storage.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.itlab.cloudjava.menuservice.dto.SortBy;
import ru.itlab.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.itlab.cloudjava.menuservice.storage.model.Category;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem;
import ru.itlab.cloudjava.menuservice.storage.repositories.updaters.MenuAttrUpdaterConfig;
import ru.itlab.cloudjava.menuservice.testutils.TestData;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem_;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@Import(MenuAttrUpdaterConfig.class)
@Transactional(propagation = Propagation.NEVER)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SqlGroup({
        @Sql(
                scripts = "classpath:insert-menu.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        @Sql(
                scripts = "classpath:clear-menus.sql",
                executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
        )
})
class MenuItemRepositoryImplTest {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private EntityManager em;

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

    private Long getIdByName(String name) {
        return em.createQuery("select m.id from MenuItem m where m.name= ?1", Long.class)
                .setParameter(1, name)
                .getSingleResult();
    }

    private <T, R> void assertFieldsEquality(T item, R dto, String... fields) {
        assertFieldsExistence(item, dto, fields);
        assertThat(item).usingRecursiveComparison()
                .withComparatorForType(java.math.BigDecimal::compareTo, java.math.BigDecimal.class)
                .comparingOnlyFields(fields)
                .isEqualTo(dto);
    }

    private <T, R> void assertElementsInOrder(List<T> items, Function<T, R> mapper, List<R> expectedElements) {
        var actualNames = items.stream().map(mapper).toList();
        assertThat(actualNames).containsExactlyElementsOf(expectedElements);
    }

    private <T, R> void assertFieldsExistence(T item, R dto, String... fields) {
        boolean itemFieldsMissing = Arrays.stream(fields)
                .anyMatch(field -> getField(item, field) == null);
        boolean dtoFieldsMissing = Arrays.stream(fields)
                .anyMatch(field -> getField(dto, field) == null);

        if (itemFieldsMissing || dtoFieldsMissing) {
            throw new AssertionError("One or more fields do not exist in the provided objects. Actual: %s. Expected: %s. Fields to compare: %s"
                    .formatted(item, dto, List.of(fields)));
        }
    }

    private <T> Field getField(T item, String fieldName) {
        try {
            return item.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }
}
