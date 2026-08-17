package ru.itlab.menuservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.itlab.menuservice.dto.CreateMenuRequest;
import ru.itlab.menuservice.dto.MenuItemDto;
import ru.itlab.menuservice.storage.model.MenuItem;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MenuItemMapper {

    MenuItemDto toDto(MenuItem domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MenuItem toDomain(CreateMenuRequest dto);

    List<MenuItemDto> toDtoList(List<MenuItem> domains);
}
