package ru.javaops.cloudjava.menuservice.service;

import ru.javaops.cloudjava.menuservice.dto.*;
import ru.javaops.cloudjava.menuservice.storage.model.Category;

import java.util.List;

public interface MenuService {

    MenuItemDto createMenuItem(CreateMenuRequest dto);

    void deleteMenuItem(Long id);

    MenuItemDto updateMenuItem(Long id, UpdateMenuRequest update);

    MenuItemDto getMenu(Long id);

    List<MenuItemDto> getMenusFor(Category category, SortBy sortBy);

    OrderMenuResponse getMenusForOrder(OrderMenuRequest request);
}
