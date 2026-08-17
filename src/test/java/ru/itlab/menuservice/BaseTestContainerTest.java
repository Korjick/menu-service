package ru.itlab.menuservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ActiveProfiles("test")
@SqlGroup({
        @Sql(
                scripts = "classpath:clear-menus.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        ),
        @Sql(
                scripts = "classpath:insert-menu.sql",
                executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
        )
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public abstract class BaseTestContainerTest {

    @Container
    @ServiceConnection
    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");
    static {
        postgres.start();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    protected Long getIdByName(String name) {
        return jdbcTemplate.queryForObject(
                "select id from menu_items where name = ?",
                Long.class,
                name
        );
    }

    protected <T, R> void assertFieldsEquality(T item, R dto, String... fields) {
        assertFieldsExistence(item, dto, fields);
        assertThat(item).usingRecursiveComparison()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .comparingOnlyFields(fields)
                .isEqualTo(dto);
    }

    protected <T, R> void assertElementsInOrder(List<T> items, Function<T, R> mapper, List<R> expectedElements) {
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
