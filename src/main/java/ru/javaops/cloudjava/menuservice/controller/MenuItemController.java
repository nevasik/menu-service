package ru.javaops.cloudjava.menuservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.model.Category;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuService menuService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemDto createMenuItem(@RequestBody CreateMenuRequest dto) {
        log.info("Received POST request to create MenuItem: {}", dto);
        return menuService.createMenuItem(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable("id") Long id) {
        log.info("Received request to DELETE MenuItem with id={}", id);
        menuService.deleteMenuItem(id);
    }

    @PatchMapping("/{id}")
    public MenuItemDto updateMenuItem(@PathVariable("id") Long id,
                                      @RequestBody UpdateMenuRequest update) {
        log.info("Received PATCH request to update MenuItem with id={}. Update params: {}", id, update);
        return menuService.updateMenuItem(id, update);
    }

    @GetMapping("/{id}")
    public MenuItemDto getMenu(@PathVariable("id") Long id) {
        log.info("Received request to GET MenuItem with id={}", id);
        return menuService.getMenu(id);
    }

    @GetMapping
    public List<MenuItemDto> getMenus(@RequestParam("category") String category,
                                      @RequestParam(value = "sort", defaultValue = "az") String sort) {
        log.info("Received request to GET list of MenuItems for category={}, sorted by={}", category, sort);
        return menuService.getMenusFor(Category.fromString(category), SortBy.fromString(sort));
    }
}
