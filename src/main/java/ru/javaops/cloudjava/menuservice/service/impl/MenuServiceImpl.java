package ru.javaops.cloudjava.menuservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;
import ru.javaops.cloudjava.menuservice.mapper.MenuItemMapper;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.model.Category;
import ru.javaops.cloudjava.menuservice.storage.repositories.MenuItemRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
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