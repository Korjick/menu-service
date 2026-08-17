package ru.itlab.menuservice.storage.repositories;

import ru.itlab.menuservice.dto.SortBy;
import ru.itlab.menuservice.dto.UpdateMenuRequest;
import ru.itlab.menuservice.storage.model.Category;
import ru.itlab.menuservice.storage.model.MenuItem;

import java.util.List;

public interface CustomMenuItemRepository {
    int updateMenu(Long id, UpdateMenuRequest dto);
    List<MenuItem> getMenusFor(Category category, SortBy sortBy);
}
