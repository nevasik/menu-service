package ru.javaops.cloudjava.menuservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.javaops.cloudjava.menuservice.BaseTest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.javaops.cloudjava.menuservice.testutils.TestConstants.BASE_URL;
import static ru.javaops.cloudjava.menuservice.testutils.TestData.*;

@AutoConfigureMockMvc
@SpringBootTest
public class MenuItemControllerTest extends BaseTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createMenuItem_createsItem() {
        var dto = createMenuRequest();
        var now = LocalDateTime.now();

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo(dto.getName());
                    assertThat(response.getDescription()).isEqualTo(dto.getDescription());
                    assertThat(response.getPrice()).isEqualTo(dto.getPrice());
                    assertThat(response.getTimeToCook()).isEqualTo(dto.getTimeToCook());
                    assertThat(response.getImageUrl()).isEqualTo(dto.getImageUrl());
                    assertThat(response.getIngredientCollection()).isEqualTo(dto.getIngredientCollection());
                    assertThat(response.getCreatedAt()).isAfter(now);
                    assertThat(response.getUpdatedAt()).isAfter(now);
                });
    }

    @Test
    void createMenuItem_returnsConflict_whenMenuWithThatNameInDb() {
        var dto = createMenuRequest();
        dto.setName("Cappuccino");

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deleteMenuItem_deletesItem() {
        var id = getIdByName("Cappuccino");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateMenuItem_updatesItem() {
        var update = updateMenuFullRequest();
        var id = getIdByName("Cappuccino");

        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getName()).isEqualTo(update.getName());
                    assertThat(response.getPrice()).isEqualTo(update.getPrice());
                    assertThat(response.getTimeToCook()).isEqualTo(update.getTimeToCook());
                    assertThat(response.getDescription()).isEqualTo(update.getDescription());
                    assertThat(response.getImageUrl()).isEqualTo(update.getImageUrl());
                });
    }

    @Test
    void updateMenuItem_returnsNotFound_whenItemNotInDb() {
        var update = updateMenuFullRequest();
        var id = 1000L;
        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }

    /*

    Получение корректного отсортированного списка блюд из категори, в которой есть блюда
     */

    @Test // Успешное получение блюда по идентификатору
    void getMenuItemByID_returnsMenuItem() {
        var req = getMenuRequest();
        var id = getIdByName("Cappuccino");

        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isNotNull();
                    assertThat(response.getDescription()).isNotNull();
                    assertThat(response.getPrice()).isNotNull();
                    assertThat(response.getImageUrl()).isNotNull();
                    assertThat(response.getCreatedAt()).isNotNull();
                });
    }

    @Test // Ошибка при получении несуществующего блюда
    void getMenuItemByID_returnsBAD_REQUEST_whenItemNotInDb() {
        var id = -11L;

        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test // Получение пустого списка блюд из категории, в которой нет блюд
    void getListMenuItemByCategory_returnEmptyList_whenListEmptyByCategory() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(BASE_URL)
                        .queryParam("category", "LUNCH")
                        .queryParam("sort", "az")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .hasSize(0);
    }

}
