package ru.itlab.cloudjava.menuservice.service;

import ru.itlab.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.itlab.cloudjava.menuservice.dto.MenuItemDto;
import ru.itlab.cloudjava.menuservice.dto.SortBy;
import ru.itlab.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.itlab.cloudjava.menuservice.storage.model.Category;

import java.util.List;

public interface MenuService {
    MenuItemDto createMenuItem(CreateMenuRequest dto);
    void deleteMenuItem(Long id);
    MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update);
    MenuItemDto getMenu(Long id);
    List<MenuItemDto> getMenusFor(Category category, SortBy sortBy);
}
