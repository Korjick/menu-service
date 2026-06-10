package ru.itlab.cloudjava.menuservice.storage.repositories;

import ru.itlab.cloudjava.menuservice.dto.SortBy;
import ru.itlab.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.itlab.cloudjava.menuservice.storage.model.Category;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem;

import java.util.List;

public interface CustomMenuItemRepository {
    int updateMenu(Long id, UpdateMenuRequest dto);
    List<MenuItem> getMenusFor(Category category, SortBy sortBy);
}
