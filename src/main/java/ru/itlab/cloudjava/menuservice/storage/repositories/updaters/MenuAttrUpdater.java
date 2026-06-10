package ru.itlab.cloudjava.menuservice.storage.repositories.updaters;

import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;
import ru.itlab.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.itlab.cloudjava.menuservice.storage.model.MenuItem;

import java.util.function.Function;

@AllArgsConstructor
public class MenuAttrUpdater<V> {

    private final SingularAttribute<MenuItem, V> attribute;
    private final Function<UpdateMenuRequest, V> valueExtractor;

    public void updateAttr(CriteriaUpdate<MenuItem> criteria, UpdateMenuRequest request) {
        final var dtoValue = valueExtractor.apply(request);
        if (dtoValue != null) {
            criteria.set(attribute, dtoValue);
        }
    }
}
