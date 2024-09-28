package ru.javaops.cloudjava.menuservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.javaops.cloudjava.menuservice.dto.*;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;
import ru.javaops.cloudjava.menuservice.mapper.MenuItemMapper;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.model.Category;
import ru.javaops.cloudjava.menuservice.storage.model.MenuItem;
import ru.javaops.cloudjava.menuservice.storage.model.MenuItemProjection;
import ru.javaops.cloudjava.menuservice.storage.repositories.CustomMenuItemRepositoryImpl;
import ru.javaops.cloudjava.menuservice.storage.repositories.MenuItemRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    @Autowired
    private final MenuItemRepository repository;

    @Autowired
    private final MenuItemMapper mapper;

    @Override
    public MenuItemDto createMenuItem(CreateMenuRequest dto) {
        MenuItem mapperDomain = mapper.toDomain(dto);

        try {
            return mapper.toDto(repository.save(mapperDomain));
        } catch (DataIntegrityViolationException e) {
            throw new MenuServiceException(String.format("Failed to save menuItem with id=%d, name=%s already exist",
                    mapperDomain.getId(), mapperDomain.getName()), HttpStatus.CONFLICT);
        }
    }

    @Override
    public void deleteMenuItem(Long id) {
        try {
            repository.deleteById(id);
        } catch (MenuServiceException e) {
            throw new MenuServiceException(String.format("Failed to delete MenuItem with id=%d. Error=%s", id, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update) {
        try {
            repository.updateMenu(id, update);
        } catch (DataIntegrityViolationException e) {
            throw new MenuServiceException(String.format("Failed to get by id=%d", id), HttpStatus.CONFLICT);
        }

        var menuItem = repository.findById(id).get();
        return mapper.toDto(menuItem);
    }

    @Override
    public MenuItemDto getMenu(Long id) {
        try {
            var menuItem = repository.findById(id).get();
            return mapper.toDto(menuItem);
        } catch (NoSuchElementException e) {
            throw new MenuServiceException(String.format("Failed to get MenuItem by id=%d", id), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<MenuItemDto> getMenusFor(Category category, SortBy sortBy) {
        try {
            return mapper.toDtoList(repository.getMenusFor(category, sortBy));
        } catch (DataIntegrityViolationException e) {
            throw new MenuServiceException(String.format("Failed to get menuItem by category %s and sortBy=%s", category, sortBy), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}