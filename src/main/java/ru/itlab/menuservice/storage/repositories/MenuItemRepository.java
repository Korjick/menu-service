package ru.itlab.menuservice.storage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itlab.menuservice.storage.model.MenuItem;


public interface MenuItemRepository extends JpaRepository<MenuItem, Long>, CustomMenuItemRepository {

}
