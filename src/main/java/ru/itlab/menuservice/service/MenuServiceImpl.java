package ru.itlab.menuservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itlab.menuservice.dto.CreateMenuRequest;
import ru.itlab.menuservice.dto.MenuItemDto;
import ru.itlab.menuservice.dto.SortBy;
import ru.itlab.menuservice.dto.UpdateMenuRequest;
import ru.itlab.menuservice.exception.MenuServiceException;
import ru.itlab.menuservice.mapper.MenuItemMapper;
import ru.itlab.menuservice.storage.model.Category;
import ru.itlab.menuservice.storage.repositories.MenuItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MenuServiceImpl implements MenuService {

    private final MenuItemMapper mapper;
    private final MenuItemRepository repository;

    @Override
    public MenuItemDto createMenuItem(CreateMenuRequest dto) {
        var menu = mapper.toDomain(dto);
        try {
            return mapper.toDto(repository.save(menu));
        } catch (DataIntegrityViolationException e) {
            var msg = String.format("Failed to create MenuItem: %s. Reason: Item with name %s already exists.", dto, dto.getName());
            throw new MenuServiceException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public void deleteMenuItem(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update) {
        try {
            int updateCount = repository.updateMenu(id, update);
            if (updateCount == 0) {
                var msg = String.format("MenuItem with id=%d not found.", id);
                throw new MenuServiceException(msg, HttpStatus.NOT_FOUND);
            }
            return getMenu(id);
        } catch (DataIntegrityViolationException ex) {
            var msg = String.format("Failed to update MenuItem with ID: %d. Reason: Item with name %s already exists.",
                    id, update.getName());
            throw new MenuServiceException(msg, HttpStatus.CONFLICT);
        }
    }

    @Override
    public MenuItemDto getMenu(Long id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> {
                    var msg = String.format("MenuItem with id=%d not found.", id);
                    return new MenuServiceException(msg, HttpStatus.NOT_FOUND);
                });
    }

    @Override
    public List<MenuItemDto> getMenusFor(Category category, SortBy sortBy) {
        return mapper.toDtoList(repository.getMenusFor(category, sortBy));
    }
}
