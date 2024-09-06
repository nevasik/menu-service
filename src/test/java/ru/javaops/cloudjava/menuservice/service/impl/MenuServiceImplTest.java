package ru.javaops.cloudjava.menuservice.service.impl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.javaops.cloudjava.menuservice.BaseIntegrationTest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.model.Category;
import ru.javaops.cloudjava.menuservice.storage.repositories.MenuItemRepository;
import ru.javaops.cloudjava.menuservice.testutils.TestData;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertThrows;

public class MenuServiceImplTest extends BaseIntegrationTest {

    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuItemRepository repository;

    @Test
    void getMenu_returnsMenu_whenMenuInDb() {
        var id = getIdByName("Cappuccino");
        var menu = menuService.getMenu(id);
        assertThat(menu).isNotNull();
        assertThat(menu.getName()).isEqualTo("Cappuccino");
        assertThat(menu.getId()).isNotNull();
        assertThat(menu.getCreatedAt()).isNotNull();
        assertThat(menu.getUpdatedAt()).isNotNull();
    }

    @Test
    void getMenu_throws_whenNoMenuInDb() {
        assertThrows(
                MenuServiceException.class,
                () -> menuService.getMenu(1000L)
        );
    }

    @Test
    void getMenusFor_DRINKS_returnsCorrectList() {
        List<MenuItemDto> drinks = menuService.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItemDto::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void deleteMenuItem_deletesItem() {
        var id = getIdByName("Cappuccino");
        menuService.deleteMenuItem(id);
        var deletedOpt = repository.findById(id);
        assertThat(deletedOpt).isEmpty();
    }

    @Test
    void createMenuItem_createsMenuItem() {
        var dto = TestData.createMenuRequest();
        // Вычитаем некоторое количество наносекунд из-за возможных проблем со сравнением дат (проявляется на Windows,
        // при тестировании на Ubuntu и Mac такой проблемы не возникало)
        // так как Postgres не поддерживает точность дат до наносекунд из коробки
        var now = LocalDateTime.now().minusNanos(1000);
        MenuItemDto result = menuService.createMenuItem(dto);
        assertThat(result.getId()).isNotNull();
        assertFieldsEquality(result, dto, "name", "description", "price", "imageUrl", "timeToCook");
        assertThat(result.getCreatedAt()).isAfter(now);
        assertThat(result.getUpdatedAt()).isAfter(now);
    }

    @Test
    void createMenuItem_throwsWhenItemWithThatNameExists() {
        var dto = TestData.createMenuRequest();
        dto.setName("Cappuccino");
        assertThrows(
                MenuServiceException.class,
                () -> menuService.createMenuItem(dto)
        );
    }

    @Test
    void updateMenuItem_updatesMenuItem_whenItemPresentInDb() {
        var id = getIdByName("Cappuccino");
        var update = TestData.updateMenuFullRequest();
        MenuItemDto updated = menuService.updateMenuItem(id, update);
        assertFieldsEquality(updated, update, "name", "description", "price", "timeToCook", "imageUrl");
    }

    @Test
    void updateMenuItem_throws_whenNoItemInDb() {
        var id = 1000L;
        var update = TestData.updateMenuFullRequest();
        assertThrows(
                MenuServiceException.class,
                () -> menuService.updateMenuItem(id, update)
        );
    }

    @Test
    void updateMenuItem_throws_whenUpdateRequestContainsNotUniqueName() {
        var id = getIdByName("Cappuccino");
        var update = TestData.updateMenuFullRequest();
        update.setName("Wine");
        assertThrows(MenuServiceException.class,
                () -> menuService.updateMenuItem(id, update));
    }
}
