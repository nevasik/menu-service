package ru.javaops.cloudjava.menuservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.dto.SortBy;
import ru.javaops.cloudjava.menuservice.dto.UpdateMenuRequest;
import ru.javaops.cloudjava.menuservice.service.MenuService;
import ru.javaops.cloudjava.menuservice.storage.model.Category;

import java.util.List;

@Tag(name = "MenuItemController", description = "REST API для работы с меню.")
@Slf4j
@RestController
@RequestMapping("/v1/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuService menuService;

    @Operation(
            summary = "${api.menu-create.summary}",
            description = "${api.menu-create.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "${api.response.createOk}"),
            @ApiResponse(
                    responseCode = "409",
                    description = "${api.response.createConflict}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.createBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )),
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemDto createMenuItem(@RequestBody @Valid CreateMenuRequest dto) {
        log.info("Received POST request to create MenuItem: {}", dto);
        return menuService.createMenuItem(dto);
    }

    @Operation(
            summary = "${api.menu-delete.summary}",
            description = "${api.menu-delete.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "${api.response.deleteNoContent}")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable("id") @Positive(message = "id должен быть > 0") Long id) {
        log.info("Received request to DELETE MenuItem with id={}", id);
        menuService.deleteMenuItem(id);
    }

    @Operation(
            summary = "${api.menu-update.summary}",
            description = "${api.menu-update.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.updateOk}"),
            @ApiResponse(
                    responseCode = "404",
                    description = "${api.response.notFound}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.updateBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
    })
    @PatchMapping("/{id}")
    public MenuItemDto updateMenuItem(@PathVariable("id") @Positive(message = "id должен быть > 0.") Long id,
                                      @RequestBody @Valid UpdateMenuRequest update) {
        log.info("Received PATCH request to update MenuItem with id={}. Update params: {}", id, update);
        return menuService.updateMenuItem(id, update);
    }

    @Operation(
            summary = "${api.menu-get.summary}",
            description = "${api.menu-get.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getOk}"),
            @ApiResponse(
                    responseCode = "404",
                    description = "${api.response.notFound}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/{id}")
    public MenuItemDto getMenu(@PathVariable("id") @Positive(message = "id должен быть > 0.") Long id) {
        log.info("Received request to GET MenuItem with id={}", id);
        return menuService.getMenu(id);
    }

    @Operation(
            summary = "${api.menu-list-get.summary}",
            description = "${api.menu-list-get.description}"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "${api.response.getListOk}"),
            @ApiResponse(
                    responseCode = "400",
                    description = "${api.response.getListBadRequest}",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping
    public List<MenuItemDto> getMenus(@RequestParam("category") @NotBlank(message = "Категория не должна быть пустой.") String category,
                                      @RequestParam(value = "sort", defaultValue = "az") @NotBlank(message = "Параметр сортировки не должен быть пустым.") String sort) {
        log.info("Received request to GET list of MenuItems for category={}, sorted by={}", category, sort);
        return menuService.getMenusFor(Category.fromString(category), SortBy.fromString(sort));
    }
}
