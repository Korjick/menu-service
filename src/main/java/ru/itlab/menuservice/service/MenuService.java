package ru.itlab.menuservice.service;

import ru.itlab.menuservice.dto.CreateMenuRequest;
import ru.itlab.menuservice.dto.MenuItemDto;
import ru.itlab.menuservice.dto.SortBy;
import ru.itlab.menuservice.dto.UpdateMenuRequest;
import ru.itlab.menuservice.storage.model.Category;

import java.util.List;

public interface MenuService {
    MenuItemDto createMenuItem(CreateMenuRequest dto);
    void deleteMenuItem(Long id);
    MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update);
    MenuItemDto getMenu(Long id);
    List<MenuItemDto> getMenusFor(Category category, SortBy sortBy);
}
