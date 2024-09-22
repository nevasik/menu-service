package ru.javaops.cloudjava.menuservice.storage.repositories.updaters;

import jakarta.persistence.criteria.CriteriaUpdate;
import lombok.AllArgsConstructor;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.storage.model.MenuItem;

import java.util.Arrays;

@AllArgsConstructor
public class MenuAttrUpdater<V> {
    public void updateAttr(CriteriaUpdate<MenuItem> criteria, UpdateMenuRequest dto) {
        Arrays.stream(dto.getClass()
                .getDeclaredFields())
                .forEach(el -> {
                    el.setAccessible(true);
                    try {
                        if (el.get(dto.getClass()) != null) {
                            criteria.set(el.getName(), el.get(dto));
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
        );
    }
}
