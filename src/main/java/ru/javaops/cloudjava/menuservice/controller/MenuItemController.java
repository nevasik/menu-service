package ru.javaops.cloudjava.menuservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;
import ru.javaops.cloudjava.menuservice.mapper.MenuItemMapper;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.model.Category;
import ru.javaops.cloudjava.menuservice.storage.model.Ingredient;
import ru.javaops.cloudjava.menuservice.storage.model.IngredientCollection;
import ru.javaops.cloudjava.menuservice.storage.model.MenuItem;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

/*
POST /v1/menu-items - создать блюдо, информация о блюде передается в теле запроса. Доступно для сотрудников, информация о сотруднике передается в токене доступа.

DELETE /v1/menu-items/{id} - удалить блюдо. Доступно для сотрудников, информация о сотруднике передается в токене доступа

PATCH /v1/menu-items/{id} - обновить блюдо, параметры обновления передаются в теле запроса. Доступно для сотрудников, информация о сотруднике передается в токене доступа

GET /v1/menu-items/{id} - получить блюдо. Доступно всем пользователям

GET /v1/menu-items?category={category}&sort={sort} - получить список блюд из выбранной категории, отсортированный или по алфавиту(AZ, ZA),
или по цене (PRICE_ASC, PRICE_DESC), или по дате создания (DATE_ASC, DATE_DESC). Доступно всем пользователям
 */

@Slf4j
@RestController
@RequestMapping("/v1/menu-items")
@RequiredArgsConstructor
@CrossOrigin
public class MenuItemController {
    @Autowired
    private final MenuService menuService;
    @Autowired
    private final MenuItemMapper mapper;

    @PostMapping("/")
    public ResponseEntity<?> createMenuItem(@Valid @RequestBody CreateMenuRequest req) {
        try {
            return new ResponseEntity<>(menuService.createMenuItem(req), HttpStatus.CREATED);
        } catch (MenuServiceException e) {
            log.error("Error create menuItem by req={} {}", req, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMenuItemByID(@PathVariable("id") Long id) {
        try {
            menuService.deleteMenuItem(id);
            return ResponseEntity.noContent().build();
        } catch (MenuServiceException e) {
            log.error("Error delete menuItem by id={} {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateMenuItemByID(@PathVariable Long id, @Valid @RequestBody UpdateMenuRequest req) {
        try {
            return new ResponseEntity<>(menuService.updateMenuItem(id, req), HttpStatus.OK);
        } catch (MenuServiceException e) {
            log.error("Error update menuItem by id={} {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMenuItemByID(@PathVariable Long id) {
        try {
            return new ResponseEntity<>(menuService.getMenu(id), HttpStatus.OK);
        } catch (MenuServiceException e) {
            log.error("Error get menuItem by id={} {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/?category={category}&sort={sort}")
    public ResponseEntity<?> getMenusFor(@PathVariable Category category, @PathVariable SortBy sort) {
        try {
            return new ResponseEntity<>(menuService.getMenusFor(category, sort), HttpStatus.OK);
        } catch (MenuServiceException e) {
            log.error("Error get menus for by category={} and sort={}. {}", category, sort, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
