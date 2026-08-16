package ru.itlab.cloudjava.menuservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.itlab.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.itlab.cloudjava.menuservice.dto.MenuItemDto;
import ru.itlab.cloudjava.menuservice.dto.SortBy;
import ru.itlab.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.itlab.cloudjava.menuservice.exception.MenuServiceException;
import ru.itlab.cloudjava.menuservice.mapper.MenuItemMapper;
import ru.itlab.cloudjava.menuservice.storage.model.Category;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem;
import ru.itlab.cloudjava.menuservice.storage.repositories.MenuItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MenuServiceImpl implements MenuService {

    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;

    @Override
    public MenuItemDto createMenuItem(CreateMenuRequest dto) {
        try {
            return menuItemMapper.toDto(menuItemRepository.save(menuItemMapper.toDomain(dto)));
        } catch (DataIntegrityViolationException e) {
            throw new MenuServiceException("Menu item with the same name already exists", HttpStatus.CONFLICT);
        }
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Override
    public MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update) {
        try {
            var count = menuItemRepository.updateMenu(id, update);
            if (count == 0) {
                throw new MenuServiceException("Menu item not found", HttpStatus.NOT_FOUND);
            }
        } catch (DataIntegrityViolationException e) {
            throw new MenuServiceException("Menu item with the same name already exists", HttpStatus.CONFLICT);
        }

        return getMenu(id);
    }

    @Override
    public MenuItemDto getMenu(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() ->
                new MenuServiceException("Menu item not found", HttpStatus.NOT_FOUND));
        return menuItemMapper.toDto(menuItem);
    }

    @Override
    public List<MenuItemDto> getMenusFor(Category category, SortBy sortBy) {
        return menuItemRepository.getMenusFor(category, sortBy).stream()
                .map(menuItemMapper::toDto)
                .toList();
    }
}
