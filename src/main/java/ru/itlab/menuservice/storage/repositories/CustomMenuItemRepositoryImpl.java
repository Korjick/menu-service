package ru.itlab.menuservice.storage.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.itlab.menuservice.dto.SortBy;
import ru.itlab.menuservice.dto.UpdateMenuRequest;
import ru.itlab.menuservice.storage.model.Category;
import ru.itlab.menuservice.storage.model.MenuItem;
import ru.itlab.menuservice.storage.model.MenuItem_;
import ru.itlab.menuservice.storage.repositories.updaters.MenuAttrUpdater;

import java.util.List;

@Repository
public class CustomMenuItemRepositoryImpl implements CustomMenuItemRepository {

    private final EntityManager em;
    private final List<MenuAttrUpdater<?>> updaters;

    public CustomMenuItemRepositoryImpl(EntityManager em, List<MenuAttrUpdater<?>> updaters) {
        this.em = em;
        this.updaters = updaters;
    }

    @Transactional
    @Override
    public int updateMenu(Long id, UpdateMenuRequest dto) {
        final var cb = em.getCriteriaBuilder();
        final var criteriaUpdate = cb.createCriteriaUpdate(MenuItem.class);
        final var root = criteriaUpdate.from(MenuItem.class);
        updaters.forEach(updater -> updater.updateAttr(criteriaUpdate, dto));
        criteriaUpdate.where(cb.equal(root.get(MenuItem_.id), id));
        return em.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public List<MenuItem> getMenusFor(Category category, SortBy sortBy) {
        final var cb = em.getCriteriaBuilder();
        final var criteriaQuery = cb.createQuery(MenuItem.class);
        final var root = criteriaQuery.from(MenuItem.class);
        criteriaQuery.select(root);
        criteriaQuery.where(cb.equal(root.get(MenuItem_.category), category));
        criteriaQuery.orderBy(sortBy.getOrder(cb, root));
        return em.createQuery(criteriaQuery).getResultList();
    }
}
