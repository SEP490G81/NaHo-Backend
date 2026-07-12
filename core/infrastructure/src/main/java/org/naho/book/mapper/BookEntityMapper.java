package org.naho.book.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.book.entity.BookEntity;
import org.naho.book.model.Book;

@Mapper(componentModel = "spring")
public interface BookEntityMapper {

    @Mapping(target = "coverImageFile", ignore = true)
    @Mapping(target = "topics", ignore = true)
    BookEntity domainToEntity(Book domain);

    @Mapping(target = "coverImageFileId", source = "coverImageFile.id")
    Book entityToDomain(BookEntity entity);
}
