package ru.itlab.menuservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import ru.itlab.menuservice.dto.CreateMenuRequest;
import ru.itlab.menuservice.dto.MenuItemDto;
import ru.itlab.menuservice.dto.SortBy;
import ru.itlab.menuservice.dto.UpdateMenuRequest;
import ru.itlab.menuservice.exception.MenuServiceException;
import ru.itlab.menuservice.mapper.MenuItemMapper;
import ru.itlab.menuservice.storage.model.Category;
import ru.itlab.menuservice.storage.model.MenuItem;
import ru.itlab.menuservice.storage.repositories.MenuItemRepository;
import ru.itlab.menuservice.testutils.TestData;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceImplTest {

    @Mock
    private MenuItemMapper mapper;

    @Mock
    private MenuItemRepository repository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @Test
    void getMenu_returnsDto_whenItemExists() {
        Long id = 1L;
        MenuItem item = new MenuItem();
        MenuItemDto dto = MenuItemDto.builder().id(id).name("Cappuccino").build();

        when(repository.findById(id)).thenReturn(Optional.of(item));
        when(mapper.toDto(item)).thenReturn(dto);

        MenuItemDto result = menuService.getMenu(id);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        verify(repository).findById(id);
        verify(mapper).toDto(item);
    }

    @Test
    void getMenu_throwsNotFound_whenItemDoesNotExist() {
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenu(id))
                .isInstanceOf(MenuServiceException.class);

        verify(repository).findById(id);
        verifyNoInteractions(mapper);
    }

    @Test
    void createMenuItem_createsItem_whenValid() {
        CreateMenuRequest request = TestData.createMenuRequest();
        MenuItem entity = new MenuItem();
        MenuItem savedEntity = new MenuItem();
        MenuItemDto expectedDto = MenuItemDto.builder().id(1L).name(request.getName()).build();

        when(mapper.toDomain(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDto(savedEntity)).thenReturn(expectedDto);

        MenuItemDto result = menuService.createMenuItem(request);

        assertThat(result).isEqualTo(expectedDto);
        verify(repository).save(entity);
    }

    @Test
    void createMenuItem_throwsConflict_whenNameAlreadyExists() {
        CreateMenuRequest request = TestData.createMenuRequest();
        MenuItem entity = new MenuItem();

        when(mapper.toDomain(request)).thenReturn(entity);
        when(repository.save(entity)).thenThrow(new DataIntegrityViolationException("Duplicate key"));

        assertThatThrownBy(() -> menuService.createMenuItem(request))
                .isInstanceOf(MenuServiceException.class);
    }

    @Test
    void deleteMenuItem_callsRepositoryDelete() {
        Long id = 1L;
        menuService.deleteMenuItem(id);
        verify(repository).deleteById(id);
    }

    @Test
    void updateMenuItem_throwsNotFound_whenUpdateCountIsZero() {
        Long id = 1L;
        UpdateMenuRequest update = TestData.updateMenuFullRequest();
        when(repository.updateMenu(id, update)).thenReturn(0);

        assertThatThrownBy(() -> menuService.updateMenuItem(id, update))
                .isInstanceOf(MenuServiceException.class);
    }

    @Test
    void getMenusFor_returnsMappedList() {
        Category category = Category.DRINKS;
        SortBy sortBy = SortBy.AZ;
        List<MenuItem> entities = List.of(new MenuItem());
        List<MenuItemDto> dtos = List.of(MenuItemDto.builder().name("Tea").build());

        when(repository.getMenusFor(category, sortBy)).thenReturn(entities);
        when(mapper.toDtoList(entities)).thenReturn(dtos);

        List<MenuItemDto> result = menuService.getMenusFor(category, sortBy);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Tea");
    }
}
