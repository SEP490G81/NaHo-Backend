package org.naho.book.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.command.UpdateBookCommand;
import org.naho.book.dto.request.UpdateBookRequest;

@Mapper(componentModel = "spring")
public interface BookRequestMapper {
    @Mapping(target = "bookId", source = "bookId")
    @Mapping(target = "adminUserId", source = "adminUserId")
    @Mapping(target = "isAdminOrManager", source = "isAdminOrManager")
    UpdateBookCommand toUpdateCommand(UpdateBookRequest request, Long bookId, Long adminUserId, boolean isAdminOrManager);
}
