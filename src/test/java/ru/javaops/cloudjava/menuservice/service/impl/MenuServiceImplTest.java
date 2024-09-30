package ru.javaops.cloudjava.menuservice.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import ru.javaops.cloudjava.menuservice.BaseTest;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.exception.MenuServiceException;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.model.Category;
import ru.javaops.cloudjava.menuservice.storage.repositories.MenuItemRepository;
import ru.javaops.cloudjava.menuservice.testutils.TestData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static ru.javaops.cloudjava.menuservice.testutils.TestData.italianSaladIngredients;

@SpringBootTest
public class MenuServiceImplTest extends BaseTest {
    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuItemRepository repository;

    @Test
    void getMenusFor_DRINKS_returnsCorrectList() {
        List<MenuItemDto> drinks = menuService.getMenusFor(Category.DRINKS, SortBy.AZ);
        assertThat(drinks).hasSize(3);
        assertElementsInOrder(drinks, MenuItemDto::getName, List.of("Cappuccino", "Tea", "Wine"));
    }

    @Test
    void createMenuItem_createsMenuItem() {
        var dto = TestData.createMenuRequest();
        /*
         Вычитаем некоторое количество наносекунд из-за возможных проблем со сравнением дат (проявляется на Windows,
         при тестировании на Ubuntu и Mac такой проблемы не возникало) так как Postgres не поддерживает точность дат
         до наносекунд из коробки
         */
        var now = LocalDateTime.now().minusNanos(1000);
        MenuItemDto result = menuService.createMenuItem(dto);
        assertThat(result.getId()).isNotNull();
        assertFieldsEquality(result, dto, "name", "description", "price", "imageUrl", "timeToCook");
        assertThat(result.getCreatedAt()).isAfter(now);
        assertThat(result.getUpdatedAt()).isAfter(now);
    }

    @Test // get menus by id
    void getMenus_returnsMenuItem_whenMenuIDInDB() {
        var id = getIdByName("Cappuccino");
        MenuItemDto menu = menuService.getMenu(id);

        assertThat(menu).isNotNull();
        assertThat(menu.getName()).isEqualTo("Cappuccino");
        assertThat(menu.getId()).isNotNull();
        assertThat(menu.getCreatedAt()).isNotNull();
        assertThat(menu.getUpdatedAt()).isNotNull();
    }

    @Test // get menus by undefined id
    void getMenuItemBy_returnsError() {
        assertThrows(MenuServiceException.class,
                () -> menuService.getMenu(1000L));
    }

    @Test // delete menu item by id
    void deleteMenuItemByID_returnsNonError() {
        var id = getIdByName("Cappuccino");
        assertDoesNotThrow(() ->  menuService.deleteMenuItem(id));

        var deleteCount =  repository.findById((id));
        assertThat(deleteCount).isEmpty();
    }

    @Test // create menuItem with duplicate name, expecting an error
    void createMenuItemWithExistName_returnsError() {
        var req = CreateMenuRequest.builder()
                .name("Tea")  // Дубликат имени
                .price(BigDecimal.valueOf(100))
                .description("Exists tea in DB")
                .imageUrl("http://test/")
                .category(Category.SALADS)
                .ingredientCollection(italianSaladIngredients())
                .build();

        MenuServiceException exception = assertThrows(MenuServiceException.class,
                () -> menuService.createMenuItem(req));

        boolean isDuplicateError = Objects.equals(
                exception.getStatus(),
                HttpStatus.CONFLICT
        );

        Assertions.assertTrue(isDuplicateError);
    }

    @Test // update menuItem success
    void updateMenuItem_returnsSuccess() {
        var id = getIdByName("Cappuccino");

        var item = UpdateMenuRequest.builder()
                .name("Test")
                .price(BigDecimal.valueOf(100))
                .description("Test nob in DB")
                .imageUrl("http://test/")
                .timeToCook(10L)
                .build();

        MenuItemDto updated = menuService.updateMenuItem(id, item);
        assertFieldsEquality(item, updated, "name", "description", "price", "", "timeToCook", "imageUrl");
    }

    @Test // update menuItem by unknown id
    void updateMenuItemNotFound_returnsError() {
        var req = UpdateMenuRequest.builder()
                .name("Test")
                .price(BigDecimal.valueOf(100))
                .description("Test nob in DB")
                .imageUrl("http://test/")
                .timeToCook(10L)
                .build();

        assertThrows(MenuServiceException.class,
                () -> menuService.updateMenuItem(-11L, req));
    }

    @Test // update menuItem by not unique name
    void updateMenuItemNotUniqueName_returnsError() {
        var id = getIdByName("Wine");
        var req = UpdateMenuRequest.builder()
                .name("Cappuccino")
                .price(BigDecimal.valueOf(100))
                .description("Test nob in DB")
                .imageUrl("http://test/")
                .timeToCook(10L)
                .build();

        MenuServiceException exception = assertThrows(MenuServiceException.class,
                () -> menuService.updateMenuItem(id, req));

        boolean isDuplicateError = Objects.equals(
                exception.getStatus(),
                HttpStatus.CONFLICT
        );

        Assertions.assertTrue(isDuplicateError);
    }
}
