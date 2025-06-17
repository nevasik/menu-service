package ru.javaops.cloudjava.menuservice.controller;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import ru.javaops.cloudjava.menuservice.BaseIntegrationTest;
import ru.javaops.cloudjava.menuservice.dto.MenuInfo;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.OrderMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.OrderMenuResponse;
import ru.javaops.cloudjava.menuservice.testutils.AuthToken;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.web.reactive.function.BodyInserters.fromFormData;
import static ru.javaops.cloudjava.menuservice.testutils.TestConstants.BASE_URL;
import static ru.javaops.cloudjava.menuservice.testutils.TestData.createMenuRequest;
import static ru.javaops.cloudjava.menuservice.testutils.TestData.updateMenuFullRequest;

public class MenuItemControllerTest extends BaseIntegrationTest {

    private static final KeycloakContainer KEYCLOAK = new KeycloakContainer("quay.io/keycloak/keycloak:24.0")
            .withRealmImportFile("/cloud-java-realm.json");

    static {
        KEYCLOAK.start();
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> KEYCLOAK.getAuthServerUrl() + "/realms/cloud-java");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> KEYCLOAK.getAuthServerUrl() + "/realms/cloud-java/protocol/openid-connect/certs");
    }

    @Autowired
    private WebTestClient webTestClient;

    private static AuthToken admin;
    private static AuthToken user;

    @BeforeAll
    static void setup() {
        WebClient webClient = WebClient.builder()
                .baseUrl(KEYCLOAK.getAuthServerUrl() + "/realms/cloud-java/protocol/openid-connect/token")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
        admin = createToken(webClient, "alex", "password");
        user = createToken(webClient, "max", "password");
    }

    @Test
    void getMenu_returnsMenu_whenItExists() {
        var id = getIdByName("Cappuccino");
        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MenuItemDto.class)
                .value(response -> {
                    assertThat(response.getId()).isNotNull();
                    assertThat(response.getName()).isEqualTo("Cappuccino");
                });
    }

    @Test
    void getMenus_returnsEmptyListForCategoryNotPresentInDb() {
        webTestClient.get()
                .uri(BASE_URL + "?category=lunch&sort=az")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .value(response -> {
                    assertThat(response).isEmpty();
                });
    }

    @Test
    void getMenu_returnsNotFound_whenItemNotExists() {
        var id = 1000L;
        webTestClient.get()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getMenus_returnsCorrectListForDRINKS_sortedByAZ() {
        webTestClient.get()
                .uri(BASE_URL + "?category=drinks&sort=az")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MenuItemDto.class)
                .value(items -> {
                    assertThat(items).hasSize(3);
                    assertThat(items.get(0).getName()).isEqualTo("Cappuccino");
                    assertThat(items.get(1).getName()).isEqualTo("Tea");
                    assertThat(items.get(2).getName()).isEqualTo("Wine");
                });
    }

    @Test
    void createMenuItem_createsItem() {
        var dto = createMenuRequest();
        var now = LocalDateTime.now();

        webTestClient.post()
                .uri(BASE_URL)
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
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
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void createMenuItem_returnsUnauthorized_whenNoAccessToken() {
        var dto = createMenuRequest();

        webTestClient.post()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createMenuItem_returnsForbidden_forSimpleUser() {
        var dto = createMenuRequest();

        webTestClient.post()
                .uri(BASE_URL)
                .headers(h -> h.setBearerAuth(user.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void deleteMenuItem_deletesItem() {
        var id = getIdByName("Cappuccino");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteMenuItem_returnsUnauthorized_whenNoAccessToken() {
        var id = getIdByName("Cappuccino");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void deleteMenuItem_returnsForbidden_forSimpleUser() {
        var id = getIdByName("Cappuccino");
        webTestClient.delete()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(user.getAccessToken()))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void updateMenuItem_updatesItem() {
        var update = updateMenuFullRequest();
        var id = getIdByName("Cappuccino");

        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
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
                .headers(h -> h.setBearerAuth(admin.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateMenuItem_returnsUnauthorized_whenNoAccessToken() {
        var update = updateMenuFullRequest();
        var id = 1000L;
        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void updateMenuItem_returnsForbidden_forSimpleUser() {
        var update = updateMenuFullRequest();
        var id = 1000L;
        webTestClient.patch()
                .uri(BASE_URL + "/" + id)
                .headers(h -> h.setBearerAuth(user.getAccessToken()))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void getMenusForOrder_returnsCorrectMenuInfo() {
        var request = OrderMenuRequest.builder()
                .menuNames(Set.of("Cappuccino", "Green Salad", "Wine", "Tea", "Unknown"))
                .build();

        webTestClient.post()
                .uri(BASE_URL + "/menu-info")
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderMenuResponse.class)
                .value(response -> {
                    var infos = response.getMenuInfos();
                    infos.sort(Comparator.comparing(MenuInfo::getName));
                    assertThat(infos).hasSize(request.getMenuNames().size());
                    assertThat(infos.get(0).getName()).isEqualTo("Cappuccino");
                    assertThat(infos.get(0).getPrice()).isNotNull();
                    assertThat(infos.get(0).getIsAvailable()).isTrue();
                    assertThat(infos.get(1).getName()).isEqualTo("Green Salad");
                    assertThat(infos.get(1).getPrice()).isNotNull();
                    assertThat(infos.get(1).getIsAvailable()).isTrue();
                    assertThat(infos.get(2).getName()).isEqualTo("Tea");
                    assertThat(infos.get(2).getPrice()).isNotNull();
                    assertThat(infos.get(2).getIsAvailable()).isTrue();
                    assertThat(infos.get(3).getName()).isEqualTo("Unknown");
                    assertThat(infos.get(3).getPrice()).isNull();
                    assertThat(infos.get(3).getIsAvailable()).isFalse();
                    assertThat(infos.get(4).getName()).isEqualTo("Wine");
                    assertThat(infos.get(4).getPrice()).isNotNull();
                    assertThat(infos.get(4).getIsAvailable()).isTrue();
                });
    }

    private static AuthToken createToken(WebClient webClient, String username, String password) {
        return webClient.post()
                .body(fromFormData("grant_type", "password")
                        .with("client_id", "cloud-java-gateway")
                        .with("username", username)
                        .with("password", password)
                        .with("client_secret", "iaDMVOKEGssvW5XRaaqZN4EO3lkvdRu6")
                )
                .retrieve()
                .bodyToMono(AuthToken.class)
                .block();
    }
}
