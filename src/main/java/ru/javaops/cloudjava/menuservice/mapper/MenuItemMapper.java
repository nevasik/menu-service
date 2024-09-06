package ru.javaops.cloudjava.menuservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.javaops.cloudjava.menuservice.dto.CreateMenuRequest;
import ru.javaops.cloudjava.menuservice.dto.MenuItemDto;
import ru.javaops.cloudjava.menuservice.storage.model.MenuItem;

import java.util.List;

/**
 * Используем библиотеку MapStruct для упрощения процесса мапинга DTO в модели и наоборот.
 * @link <a href="https://mapstruct.org/">MapStruct</a>
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface MenuItemMapper {

    MenuItemDto toDto(MenuItem domain);

    // в запросе на создание блюда не будет полей id, createdAt, updatedAt, они
    // проставляются hibernate автоматически, поэтому говорим MapStruct игнорировать их
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MenuItem toDomain(CreateMenuRequest dto);

    List<MenuItemDto> toDtoList(List<MenuItem> domains);
}
